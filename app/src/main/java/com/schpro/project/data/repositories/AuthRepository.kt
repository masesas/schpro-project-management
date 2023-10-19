package com.schpro.project.data.repositories

import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.request.LoginRequest
import com.schpro.project.data.models.request.RegistrasiRequest

interface AuthRepository {
    fun register(request: RegistrasiRequest, result: (Resource<String>) -> Unit)

    fun login(request: LoginRequest, result: (Resource<String>) -> Unit)

    fun logout(result: () -> Unit)
}