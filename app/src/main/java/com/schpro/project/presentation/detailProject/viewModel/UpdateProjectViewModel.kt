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
class UpdateProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
) : BaseViewModel() {
    private val _updateProject = SingleLiveData<UiState<String>>()

    val updateProject: LiveData<UiState<String>?>
        get() = _updateProject

    fun updateProject(project: Project) {
        _updateProject.value = UiState.Loading
        viewModelScope.launch {
            projectRepository.updateProject(project).run {
                when (this) {
                    is Resource.Success -> _updateProject.value = UiState.Success(data)
                    is Resource.Failure -> _updateProject.value = UiState.Failure(message)
                }
            }
        }
    }

}