package com.schpro.project.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.schpro.project.core.FireStoreCollection
import com.schpro.project.core.FireStoreFields
import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Dashboard
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Status
import com.schpro.project.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProjectRepositoryImpl(
    private val database: FirebaseFirestore
) : ProjectRepository {
    override suspend fun getProjects(user: User) = callbackFlow {
        var document = database.collection(FireStoreCollection.PROJECT)
            .orderBy("createdDate", Query.Direction.DESCENDING)

        document = when (user.role) {
            Roles.ProjectTeam -> {
                document.whereArrayContains(FireStoreFields.MEMBERS, user)
            }

            Roles.ProjectManager -> {
                document.whereEqualTo(FireStoreFields.BY_USER, user.id)
            }
        }

        val snapshotListener = document
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

    override suspend fun getProjectById(id: String) = callbackFlow {
        val document = database.collection(FireStoreCollection.PROJECT).document(id)

        val snapshotListener = document.addSnapshotListener { snapshot, e ->
            val result = if (snapshot != null) {
                Resource.Success(snapshot.toObject(Project::class.java)!!)
            } else {
                Resource.Failure(e?.localizedMessage)
            }

            trySend(result)
        }

        awaitClose {
            snapshotListener.remove()
        }
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

    override suspend fun getProjectsCount(user: User) = callbackFlow {
        val document = database
            .collection(FireStoreCollection.PROJECT)
            .whereEqualTo(FireStoreFields.BY_USER, user.id)

        val snapshotListener = document
            .orderBy(FireStoreFields.CREATED_DATE, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val result = snapshot?.toObjects(Project::class.java)
                val dashboard = Dashboard()
                if (result != null) {
                    dashboard.totalProject = result.size
                    for (data in result) {
                        when (data.status) {
                            Status.OnGoing -> {
                                dashboard.totalProjectOnGoing += 1
                            }

                            Status.Complete -> dashboard.totalProjectComplete += 1
                            else -> {}
                        }
                    }
                }
                trySend(Resource.Success(dashboard))
            }

        awaitClose {
            snapshotListener.remove()
        }
    }
}