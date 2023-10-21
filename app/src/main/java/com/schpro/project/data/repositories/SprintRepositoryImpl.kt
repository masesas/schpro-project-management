package com.schpro.project.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.schpro.project.core.FireStoreCollection
import com.schpro.project.core.FireStoreFields
import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Sprint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SprintRepositoryImpl(
    private val database: FirebaseFirestore
) : SprintRepository {
    override suspend fun getSprints(projectId: String) = callbackFlow {
        val document = database
            .collection(FireStoreCollection.SPRINT)
            .whereEqualTo(FireStoreFields.PROJECT_ID, projectId)

        val snapshotListener = document
            .orderBy(FireStoreFields.CREATED_DATE, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val result = if (snapshot != null) {
                    println("result sprint >>> ${snapshot.toObjects(Sprint::class.java)}")
                    Resource.Success(snapshot.toObjects(Sprint::class.java))
                } else {
                    Resource.Failure(e?.localizedMessage)
                }

                trySend(result)
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getSprintById(id: String) = callbackFlow {
        val document = database.collection(FireStoreCollection.SPRINT).document(id)

        val snapshotListener = document.addSnapshotListener { snapshot, e ->
            val result = if (snapshot != null) {
                Resource.Success(snapshot.toObject(Sprint::class.java)!!)
            } else {
                Resource.Failure(e?.localizedMessage)
            }

            trySend(result)
        }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun saveSprint(sprint: Sprint) = try {
        if (sprint.projectId.isEmpty() || sprint.byUser == null) throw Exception("Invalid project id or user id")

        val sprintDoc = database.collection(FireStoreCollection.SPRINT).document()
        sprint.id = sprintDoc.id
        sprintDoc.set(sprint).await()
        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }

    override suspend fun deleteSprint(sprintId: String): Resource<Boolean> = try {
        val sprintDoc = database.collection(FireStoreCollection.SPRINT).document(sprintId)
        sprintDoc.delete().await()
        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }
}