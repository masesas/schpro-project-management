package com.schpro.project.data.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * value as percentage
 */
const val TODO_WEIGHT_PERCENT = 0
const val ONGOING_WEIGHT_PERCENT = 50
const val DONE_WEIGHT_PERCENT = 100

@Parcelize
data class Project(
    var id: String = "",
    var title: String = "",
    var members: List<User> = arrayListOf(),
    var dueDate: String = "",
    var description: String = "",
    var status: Status = Status.Todo,
    var byUser: User? = null,
    var updatedUser: User? = null,
    var progress: Int = 0,
    @ServerTimestamp
    val createdDate: Date = Date(),
    var updatedDate: Date? = null,
) : Parcelable {
    override fun toString(): String {
        return "Project(id='$id', title='$title', members=$members, dueDate='$dueDate', description='$description', byUser=$byUser, updatedUser=$updatedUser, createdDate=$createdDate, updatedDate=$updatedDate)"
    }
}

enum class Status(val optionalName: String) {
    Todo("To do"),
    OnGoing("On Going"),
    Done("Done"),
    Complete("Complete");

    companion object {
        fun fromString(value: String): Status {
            return when (value) {
                "To do", "Todo" -> Todo

                "On Going", "OnGoing" -> OnGoing

                "Done" -> Done

                else -> Status.Todo
            }

        }
    }
}
