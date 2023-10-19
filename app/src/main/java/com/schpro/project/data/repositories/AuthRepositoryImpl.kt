package com.schpro.project.data.repositories

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.schpro.project.core.COMMON_ERROR
import com.schpro.project.core.SharePrefsConstants
import com.schpro.project.core.base.Resource
import com.schpro.project.data.models.User
import com.schpro.project.data.models.request.LoginRequest
import com.schpro.project.data.models.request.RegistrasiRequest

class AuthRepositoryImpl(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val appSharePrefs: SharedPreferences,
    val gson: Gson,
    val userRepository: UserRepository
) : AuthRepository {
    override fun register(request: RegistrasiRequest, result: (Resource<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(request.email, request.password)
            .addOnCompleteListener { response ->
                if (response.isSuccessful) {
                    val user = User.fromRegist(response.result.user?.uid ?: "", request)
                    userRepository.updateUserInfo(user) { state ->
                        when (state) {
                            is Resource.Success -> {
                                userRepository.storeSession(user.id) {
                                    result.invoke(Resource.Success("Daftar berhasil"))
                                }

                            }

                            is Resource.Failure -> {
                                result.invoke(Resource.Failure("Gagal daftar, terjadi kesalahan"))
                            }
                        }
                    }
                } else {
                    try {
                        throw response.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(Resource.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(Resource.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(Resource.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(Resource.Failure(e.message ?: COMMON_ERROR))
                    }
                }
            }
            .addOnFailureListener { failure ->
                result.invoke(Resource.Failure(failure.message ?: COMMON_ERROR))
            }
    }

    override fun login(request: LoginRequest, result: (Resource<String>) -> Unit) {
        if (auth.currentUser != null) {
            auth.signOut()
        }

        auth.signInWithEmailAndPassword(request.email, request.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val storeSession =
                        userRepository.storeSession(task.result.user?.uid ?: "") { user ->
                            if (user != null) {
                                result.invoke(Resource.Success("Berhasil login"))
                            } else {
                                result.invoke(Resource.Failure("Email dan Password tidak valid"))
                            }
                        }

                } else {
                    result.invoke(Resource.Failure("Email dan Password tidak valid"))
                }
            }
            .addOnFailureListener { }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appSharePrefs.edit().putString(SharePrefsConstants.USER_SESSION, null).apply()
        result.invoke()
    }
}