package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    var id: String = "",
    var projectId: String = "",
    var sprintId: String = "",
    var title: String = "",
    var members: MutableList<User> = arrayListOf(),
    var tenggatTask: String = "",
    var status: Status = Status.Todo,
    var description: String = "",
    var byUser: User? = null,
    var updatedUser: User? = null,
    @ServerTimestamp
    val createdDate: Date = Date(),
    var updatedDate: Date? = null,
) : Parcelable