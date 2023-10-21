package com.schpro.project.core.injection

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.schpro.project.data.repositories.AuthRepository
import com.schpro.project.data.repositories.AuthRepositoryImpl
import com.schpro.project.data.repositories.ProjectRepository
import com.schpro.project.data.repositories.ProjectRepositoryImpl
import com.schpro.project.data.repositories.SprintRepository
import com.schpro.project.data.repositories.SprintRepositoryImpl
import com.schpro.project.data.repositories.TaskRepository
import com.schpro.project.data.repositories.TaskRepositoryImpl
import com.schpro.project.data.repositories.UserRepository
import com.schpro.project.data.repositories.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        appSharePrefs: SharedPreferences,
        gson: Gson,
        userRepository: UserRepository
    ): AuthRepository = AuthRepositoryImpl(auth, database, appSharePrefs, gson, userRepository)

    @Singleton
    @Provides
    fun provideUserRepository(
        database: FirebaseFirestore,
        appSharePrefs: SharedPreferences,
        gson: Gson,
    ): UserRepository = UserRepositoryImpl(database, appSharePrefs, gson)

    @Singleton
    @Provides
    fun provideProjectRepository(database: FirebaseFirestore): ProjectRepository =
        ProjectRepositoryImpl(database)

    @Singleton
    @Provides
    fun provideSprintRepository(
        database: FirebaseFirestore
    ): SprintRepository = SprintRepositoryImpl(database)

    @Singleton
    @Provides
    fun provideTaskRepository(
        database: FirebaseFirestore
    ): TaskRepository = TaskRepositoryImpl(database)
}