package com.schpro.project.data.repositories

import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Sprint
import kotlinx.coroutines.flow.Flow

interface SprintRepository {

    suspend fun getSprints(projectId: String): Flow<Resource<List<Sprint>>>

    suspend fun getSprintById(id: String): Flow<Resource<Sprint>>

    // will update project collection
    suspend fun saveSprint(sprint: Sprint): Resource<Boolean>

    suspend fun deleteSprint(sprintId: String): Resource<Boolean>
}