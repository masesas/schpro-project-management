package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Project(
    var id: String = "",
    var title: String = "",
    var members: List<User> = arrayListOf(),
    var dueDate: String = "",
    var description: String = "",
    var byUser: User? = null,
    var updatedUser: User? = null,
    @ServerTimestamp
    val createdDate: Date = Date(),
    var updatedDate: Date? = null,
) : Parcelable {
    enum class Status(val optionalName: String) {
        Todo("To do"),
        OnGoing("On Going"),
        Done("Done")
    }

    override fun toString(): String {
        return "Project(id='$id', title='$title', members=$members, dueDate='$dueDate', description='$description', byUser=$byUser, updatedUser=$updatedUser, createdDate=$createdDate, updatedDate=$updatedDate)"
    }


}
