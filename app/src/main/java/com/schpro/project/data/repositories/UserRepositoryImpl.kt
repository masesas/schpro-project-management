package com.schpro.project.data.repositories

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.schpro.project.core.FireStoreCollection
import com.schpro.project.core.SharePrefsConstants
import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class UserRepositoryImpl(
    val database: FirebaseFirestore,
    private val appSharePrefs: SharedPreferences,
    private val gson: Gson
) : UserRepository {
    override fun updateUserInfo(user: User, result: (Resource<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(Resource.Success("Profile berhasil di perbaharui"))
            }
            .addOnFailureListener {
                result.invoke(Resource.Failure(it.localizedMessage ?: ""))
            }
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database
            .collection(FireStoreCollection.USER)
            .document(id)
            .get()
            .addOnCompleteListener {
                val user = it.result.toObject(User::class.java)
                appSharePrefs
                    .edit()
                    .putString(SharePrefsConstants.USER_SESSION, gson.toJson(user))
                    .apply()

                result.invoke(user)
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val userStr = appSharePrefs.getString(SharePrefsConstants.USER_SESSION, null)
        if (userStr != null) {
            result.invoke(gson.fromJson(userStr, User::class.java))
        } else {
            result.invoke(null)
        }
    }

    override suspend fun getUserByRole(role: Roles) = callbackFlow {
        val snapshotListener =
            database
                .collection(FireStoreCollection.USER)
                .whereEqualTo("role", role.name)
                .addSnapshotListener { snapshot, e ->
                    val result = if (snapshot != null) {
                        val users = snapshot.toObjects(User::class.java);
                        Resource.Success(users)
                    } else {
                        Resource.Failure(e?.localizedMessage)
                    }

                    trySend(result)
                }

        awaitClose {
            snapshotListener.remove()
        }
    }
}