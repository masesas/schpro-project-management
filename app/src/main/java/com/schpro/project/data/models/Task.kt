package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    val id: String,
    val sprintId: String,
    val projectId: String,
    val title: String,
    val members: MutableList<User> = arrayListOf(),
    val date: Date,
    val status: Project.Status,
    val description: String,
    @ServerTimestamp
    val createdDate: Date = Date()
) : Parcelable