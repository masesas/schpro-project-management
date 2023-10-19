package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Sprint(
    val id: String,
    val projectId: String,
    val title: String,
    val fromDate: Date,
    val toDate: Date,
    val todos: MutableList<Task> = arrayListOf(),
    val onGoing: MutableList<Task> = arrayListOf(),
    val done: MutableList<Task> = arrayListOf(),
    @ServerTimestamp
    val createdDate: Date = Date()
) : Parcelable