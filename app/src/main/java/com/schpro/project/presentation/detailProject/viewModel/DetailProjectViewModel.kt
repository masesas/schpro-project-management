package com.schpro.project.presentation.detailProject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.SingleLiveData
import com.schpro.project.core.base.UiState
import com.schpro.project.data.models.Project
import com.schpro.project.data.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
) : BaseViewModel() {
    private val _detailProject = SingleLiveData<UiState<Project>>()

    val detailProject: LiveData<UiState<Project>?>
        get() = _detailProject

    fun getDetailProject(id: String) {
        _detailProject.value = UiState.Loading
        viewModelScope.launch {
            projectRepository.getProjectById(id).run {
                when (this) {
                    is Resource.Success -> _detailProject.value = UiState.Success(data)
                    is Resource.Failure -> _detailProject.value = UiState.Failure(message)
                }
            }
        }
    }
}