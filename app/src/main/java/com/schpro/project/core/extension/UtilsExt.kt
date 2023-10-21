package com.schpro.project.core.extension

import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.UiState

fun <T> UiState<T>.fromResource(result: Resource<T>): UiState<T> = when (result) {
    is Resource.Success -> UiState.Success(result.data)
    is Resource.Failure -> UiState.Failure(result.message)
}

fun <R, S> Resource<R>.toState(result: S): UiState<S> = when (this) {
    is Resource.Success -> UiState.Success(result)
    is Resource.Failure -> UiState.Failure(message)
}

fun <T> Resource<T>.toState(): UiState<T> = when (this) {
    is Resource.Success -> UiState.Success(data)
    is Resource.Failure -> UiState.Failure(message)
}

fun <T> Resource<T>.toData(): T? = when (this) {
    is Resource.Success -> data
    is Resource.Failure -> null
}