package com.schpro.project.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.schpro.project.core.FireStoreCollection
import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProjectRepositoryImpl(
    val database: FirebaseFirestore
) : ProjectRepository {
    override suspend fun getProjects(user: User) = callbackFlow {
        val document = database.collection(FireStoreCollection.PROJECT)

        when (user.role) {
            Roles.ProjectTeam -> {
                document.whereArrayContains("members", user)
            }

            Roles.ProjectManager -> {
                document.whereEqualTo("userId", user.id)
            }
        }

        val snapshotListener = document.orderBy("createdDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val result = if (snapshot != null) {
                    Resource.Success(snapshot.toObjects(Project::class.java))
                } else {
                    Resource.Failure(e?.localizedMessage)
                }

                trySend(result)
            }

        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun getProjectById(id: String) = try {
        val document = database
            .collection(FireStoreCollection.PROJECT)
            .document(id)
            .get()
            .await()

        if (document == null) {
            Resource.Failure("Gagal meload project");
        } else {
            Resource.Success(document.toObject(Project::class.java)!!)
        }

    } catch (e: Exception) {
        Resource.Failure(e.message);
    }

    override suspend fun saveProject(project: Project) = try {
        val document = database.collection(FireStoreCollection.PROJECT).document()
        project.id = document.id

        document.set(project).await()
        Resource.Success(Pair(project, "Project berhasil ditambahkan"))
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }

    override suspend fun updateProject(project: Project) = try {
        val document = database.collection(FireStoreCollection.PROJECT).document(project.id)
        document.set(project).await()
        Resource.Success("Project berhasil di update")
    } catch (e: Exception) {
        Resource.Failure(e.message)
    }
}