package com.schpro.project.presentation.home.viewModel

import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    fun logout(result: () -> Unit) = authRepository.logout(result)
}