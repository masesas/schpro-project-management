package com.schpro.project.core.base

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()

    data class Failure(val message: String?) : Resource<Nothing>()
}