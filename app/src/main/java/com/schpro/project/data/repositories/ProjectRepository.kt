package com.schpro.project.data.repositories

import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Dashboard
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.User
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun getProjects(user: User): Flow<Resource<List<Project>>>

    suspend fun getProjectById(id: String): Flow<Resource<Project>>

    suspend fun saveProject(project: Project): Resource<Pair<Project, String>>

    suspend fun updateProject(project: Project): Resource<String>

    suspend fun getProjectsCount(user: User): Flow<Resource<Dashboard>>
}