package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    var id: String = "",
    var sprintId: String = "",
    val title: String = "",
    val members: MutableList<User> = arrayListOf(),
    val tenggatTask: String = "",
    val status: Project.Status = Project.Status.Todo,
    val description: String = "",
    var byUser: User? = null,
    var updatedUser: User? = null,
    @ServerTimestamp
    val createdDate: Date = Date(),
    var updatedDate: Date? = null,
) : Parcelable