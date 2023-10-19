package com.schpro.project.data.repositories

import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun updateUserInfo(user: User, result: (Resource<String>) -> Unit)

    fun storeSession(id: String, result: (User?) -> Unit)

    fun getSession(result: (User?) -> Unit)

    suspend fun getUserByRole(role: Roles): Flow<Resource<List<User>>>
}