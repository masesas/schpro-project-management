package com.schpro.project.presentation.home.viewModel

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
class AddProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : BaseViewModel() {
    private val _saveProject = SingleLiveData<UiState<Pair<Project, String>>>()

    val saveProject: LiveData<UiState<Pair<Project, String>>?>
        get() = _saveProject

    fun saveProject(project: Project) {
        _saveProject.value = UiState.Loading
        viewModelScope.launch {
            projectRepository.saveProject(project).run {
                when (this) {
                    is Resource.Success -> _saveProject.value = UiState.Success(data)
                    is Resource.Failure -> _saveProject.value = UiState.Failure(message)
                }
            }
        }
    }
}