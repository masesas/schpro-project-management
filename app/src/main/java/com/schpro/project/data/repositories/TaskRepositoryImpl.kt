package com.schpro.project.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.schpro.project.core.FireStoreCollection
import com.schpro.project.core.FireStoreFields
import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val database: FirebaseFirestore,
) : TaskRepository {

    override suspend fun getTasks(user: User): Flow<Resource<List<Task>>> = callbackFlow {
        val document = database.collection(FireStoreCollection.TASK)

        when (user.role) {
            Roles.ProjectTeam -> {
                document.whereArrayContains(FireStoreFields.MEMBERS, user)
            }

            Roles.ProjectManager -> {
                document.whereEqualTo(FireStoreFields.USER_ID, user.id)
            }
        }

        val snapshotListener =
            document.orderBy(FireStoreFields.CREATED_DATE, Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    val result = if (snapshot != null) {
                        Resource.Success(snapshot.toObjects(Task::class.java))
                    } else {
                        Resource.Failure(e?.localizedMessage)
                    }

                    trySend(result)
                }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getTaskById(id: String) = try {
        val document = database
            .collection(FireStoreCollection.SPRINT)
            .document(id)
            .get()
            .await()

        if(document.data != null){
            Resource.Success(document.toObject(Task::class.java)!!)
        }

        Resource.Failure("Gagal meload task detail")
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }

    override suspend fun saveTask(task: Task) = try {
        if (task.sprintId.isEmpty() || task.byUser == null)
            throw Exception("Invalid project id or sprint id or user id")

        val document = database.collection(FireStoreCollection.TASK).document()
        task.id = document.id

        document.set(task).await()
        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }

    override suspend fun updateTask(task: Task) = try {
        val document = database.collection(FireStoreCollection.TASK).document(task.id)
        document.set(task).await()
        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }
}