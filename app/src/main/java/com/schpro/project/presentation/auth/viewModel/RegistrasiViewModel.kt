package com.schpro.project.presentation.auth.viewModel

import androidx.lifecycle.MutableLiveData
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.UiState
import com.schpro.project.data.models.request.RegistrasiRequest
import com.schpro.project.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrasiViewModel @Inject constructor(
    val repository: AuthRepository
) : BaseViewModel() {
    private val _state = MutableLiveData<UiState<String>>()

    val state get() = _state

    fun register(request: RegistrasiRequest) {
        _state.value = UiState.Loading
        repository.register(request) {
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