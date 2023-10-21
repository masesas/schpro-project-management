package com.schpro.project.presentation.detailProject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.SingleLiveData
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toState
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.Sprint
import com.schpro.project.data.repositories.ProjectRepository
import com.schpro.project.data.repositories.SprintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val sprintRepository: SprintRepository
) : BaseViewModel() {
    private val _detailProject = MutableLiveData<UiState<Project>>()
    private val _saveSprint = SingleLiveData<UiState<String>>()
    private val _sprintList = MutableLiveData<UiState<List<Sprint>>>()
    private val _deleteSprint = SingleLiveData<UiState<String>>()

    val detailProject: LiveData<UiState<Project>?>
        get() = _detailProject

    val saveSprint: LiveData<UiState<String>?>
        get() = _saveSprint

    val sprintList: LiveData<UiState<List<Sprint>>>
        get() = _sprintList

    val deleteSprint: LiveData<UiState<String>?>
        get() = _deleteSprint

    fun getDetailProject(id: String) {
        _detailProject.value = UiState.Loading
        viewModelScope.launch {
            projectRepository.getProjectById(id).collect {
                _detailProject.value = it.toState()
            }
        }
    }

    fun getSprintList(projectId: String) {
        _sprintList.value = UiState.Loading
        viewModelScope.launch {
            sprintRepository.getSprints(projectId).collect { response ->
                _sprintList.value = response.toState()
            }
        }
    }

    fun saveSprint(sprint: Sprint) {
        _saveSprint.value = UiState.Loading

        getSession { user ->
            if (user != null && _detailProject.value != null) {
                sprint.byUser = user
                sprint.projectId = (_detailProject.value as UiState.Success).data.id

                viewModelScope.launch {
                    sprintRepository.saveSprint(sprint).run {
                        _saveSprint.value = this.toState("Berhasil menambahkan sprint")
                    }
                }
            } else {
                _saveSprint.value = UiState.Failure("Gagal menyimpan sprint")
            }
        }
    }

    fun deleteSprint(sprint: Sprint) {
        _deleteSprint.value = UiState.Loading
        viewModelScope.launch {
            sprintRepository.deleteSprint(sprint.id).run {
                _deleteSprint.value = this.toState("Sprint di hapus")
            }
        }
    }
}