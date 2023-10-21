package com.schpro.project.data.repositories

import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(user: User): Flow<Resource<List<Task>>>

    suspend fun getTaskById(id: String): Resource<Task>

    // will update sprint collection
    suspend fun saveTask(task: Task): Resource<Boolean>

    // will update sprint collection
    suspend fun updateTask(task: Task): Resource<Boolean>
}