package com.schpro.project.data.models

import android.os.Parcelable
import com.schpro.project.data.models.request.RegistrasiRequest
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    val email: String = "",
    val username: String = "",
    val role: Roles = Roles.ProjectTeam,
) : Parcelable {
    companion object {
        fun fromRegist(id: String, request: RegistrasiRequest): User = User(
            id = id,
            email = request.email,
            username = request.username,
            role = request.role
        )
    }
}