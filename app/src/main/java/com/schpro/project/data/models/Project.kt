package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Project(
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var members: List<User> = arrayListOf(),
    var dueDate: String = "",
    var status: Status = Status.Todo,
    var description: String = "",
    var projectManager: User? = null,
    val sprints: MutableList<Sprint> = arrayListOf(),
    @ServerTimestamp
    val createdDate: Date = Date()
) : Parcelable {
    enum class Status(val optionalName: String) {
        Todo("To do"),
        OnGoing("On Going"),
        Done("Done")
    }
}
