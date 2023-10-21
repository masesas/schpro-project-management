package com.schpro.project.presentation.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.UiState
import com.schpro.project.data.models.Dashboard
import com.schpro.project.data.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : BaseViewModel() {
    private val _countState = MutableLiveData<UiState<Dashboard>>()

    val countState: LiveData<UiState<Dashboard>>
        get() = _countState

    fun getCountDashboard() {
        _countState.value = UiState.Loading
    }
}