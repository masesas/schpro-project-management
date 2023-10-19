package com.schpro.project.presentation.auth.viewModel

import androidx.lifecycle.MutableLiveData
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.UiState
import com.schpro.project.data.models.request.LoginRequest
import com.schpro.project.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel() {
    private var _state = MutableLiveData<UiState<String>>()

    val state get() = _state

    fun login(request: LoginRequest) {
        _state.value = UiState.Loading
        repository.login(request) {
            when (it) {
                is Resource.Success -> {
                    _state.value = UiState.Success(it.data)
                }

                is Resource.Failure -> {
                    _state.value = UiState.Failure(it.message)
                }
            }
        }
    }
}