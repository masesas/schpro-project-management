package com.schpro.project.presentation.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.UiState
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.User
import com.schpro.project.data.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : BaseViewModel() {
    private val _projectList = MutableLiveData<UiState<List<Project>>>()

    val projectList: LiveData<UiState<List<Project>>>
        get() = _projectList

    fun getProjectList(session: User) {
        _projectList.value = UiState.Loading
        viewModelScope.launch {
            projectRepository.getProjects(session).collect { response ->
                when (response) {
                    is Resource.Success -> _projectList.value = UiState.Success(response.data)
                    is Resource.Failure -> _projectList.value = UiState.Failure(response.message)
                }
            }
        }
    }
}