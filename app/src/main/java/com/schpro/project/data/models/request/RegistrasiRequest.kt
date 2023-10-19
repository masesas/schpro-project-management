package com.schpro.project.data.models.request

import com.schpro.project.data.models.Roles

data class RegistrasiRequest(
    val email: String,
    val username: String,
    val role: Roles,
    val password: String,
    val passwordConfirmation: String,
)
