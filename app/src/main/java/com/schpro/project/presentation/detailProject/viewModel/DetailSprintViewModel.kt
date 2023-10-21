package com.schpro.project.presentation.detailProject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.SingleLiveData
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toState
import com.schpro.project.data.models.Dashboard
import com.schpro.project.data.models.Sprint
import com.schpro.project.data.models.Status
import com.schpro.project.data.models.Task
import com.schpro.project.data.repositories.SprintRepository
import com.schpro.project.data.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailSprintViewModel @Inject constructor(
    private val sprintRepository: SprintRepository,
    private val taskRepository: TaskRepository
) : BaseViewModel() {
    private val _taskState = MutableLiveData<Pair<Status, List<Task>>>()
    private val _saveTaskState = SingleLiveData<UiState<String>>()
    private val _sprintDetailState = MutableLiveData<UiState<Sprint>>()
    private val _countingTaskState = MutableLiveData<Dashboard>()

    val taskState: LiveData<Pair<Status, List<Task>>?>
        get() = _taskState
    val saveTaskState: LiveData<UiState<String>?>
        get() = _saveTaskState

    val sprintDetailState: LiveData<UiState<Sprint>>
        get() = _sprintDetailState

    val countTaskState: LiveData<Dashboard>
        get() = _countingTaskState

    fun getSprintDetail(id: String) {
        _sprintDetailState.value = UiState.Loading
        viewModelScope.launch {
            sprintRepository.getSprintById(id).collect { response ->
                _sprintDetailState.value = response.toState()
            }
        }
    }

    fun getTaskByStatus(sprintId: String, status: Status) {
        viewModelScope.launch {
            taskRepository.getTasks(sprintId, status).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        _taskState.value = Pair(status, response.data)
                    }

                    is Resource.Failure -> {
                        _taskState.value = Pair(status, mutableListOf())
                    }
                }
            }
        }
    }

    fun saveTask(task: Task) {
        _saveTaskState.value = UiState.Loading
        viewModelScope.launch {
            taskRepository.saveTask(task).run {
                _saveTaskState.value = toState("Task berhasil di simpan")
                getTaskByStatus(task.sprintId, Status.Todo)
            }
        }
    }

    fun updateTask(task: Task) {
        _saveTaskState.value = UiState.Loading
        viewModelScope.launch {
            taskRepository.updateTask(task).run {
                _saveTaskState.value = toState("Task berhasil di update")
                //getTaskByStatus(task.sprintId, task.status)
            }
        }
    }

    fun getCountTask(sprintId: String) {
        viewModelScope.launch {
            taskRepository.getTasksCount(sprintId, null).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        val data = response.data
                        _countingTaskState.value = data
                    }

                    is Resource.Failure -> {}
                }

            }
        }
    }
}