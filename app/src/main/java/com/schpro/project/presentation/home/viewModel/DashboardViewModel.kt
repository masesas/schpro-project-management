package com.schpro.project.presentation.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.SingleLiveData
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toData
import com.schpro.project.core.extension.toState
import com.schpro.project.data.models.DONE_WEIGHT_PERCENT
import com.schpro.project.data.models.Dashboard
import com.schpro.project.data.models.ONGOING_WEIGHT_PERCENT
import com.schpro.project.data.models.Project
import com.schpro.project.data.models.Roles
import com.schpro.project.data.models.Status
import com.schpro.project.data.models.TODO_WEIGHT_PERCENT
import com.schpro.project.data.models.Task
import com.schpro.project.data.models.User
import com.schpro.project.data.repositories.ProjectRepository
import com.schpro.project.data.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : BaseViewModel() {
    private val _countingTaskState = MutableLiveData<Dashboard>()
    private val _countingProjectState = MutableLiveData<Dashboard>()
    private val _taskState = MutableLiveData<List<Task>>()
    private val _projectListState = MutableLiveData<List<Project>>()
    private val _saveTaskState = SingleLiveData<UiState<String>>()

    val countTaskState: LiveData<Dashboard>
        get() = _countingTaskState
    val countProjectState: LiveData<Dashboard>
        get() = _countingProjectState
    val taskListState: LiveData<List<Task>>
        get() = _taskState
    val projectListState: LiveData<List<Project>>
        get() = _projectListState
    val saveTaskState: LiveData<UiState<String>?>
        get() = _saveTaskState

    private var userSession = User()

    fun initial(user: User) {
        userSession = user
        when (userSession.role) {
            Roles.ProjectTeam -> {
                getTask()
                getCountUserTeam()
            }

            Roles.ProjectManager -> {
                getProject()
                getCountUserManager()
            }
        }
    }

    private fun getTask() {
        viewModelScope.launch {
            taskRepository.getTasks(null, null, userSession).collect { response ->
                if (response.toData() != null) {
                    val data = response.toData()!!.filter { it.status != Status.Done }
                    _taskState.value = data;
                }
            }
        }
    }

    private fun getProject() {
        viewModelScope.launch {
            projectRepository.getProjects(userSession).collect { response ->
                _projectListState.value = response.toData() ?: arrayListOf()
            }
        }
    }

    private fun getCountUserTeam() {
        viewModelScope.launch {
            taskRepository.getTasksCount(null, null, userSession).collect { response ->
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

    private fun getCountUserManager() {
        viewModelScope.launch {
            projectRepository.getProjectsCount(userSession).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        val data = response.data
                        _countingProjectState.value = data
                    }

                    is Resource.Failure -> {}
                }
            }
        }
    }

    fun markAsDoneTask(task: Task) {
        _saveTaskState.value = UiState.Loading
        viewModelScope.launch {
            taskRepository.updateTask(task).run {
                _saveTaskState.value = toState("Task berhasil di update")
                //getTaskByStatus(task.sprintId, task.status)
            }
        }
    }

    fun getProjectProgressByTask(projectId: String, result: (Dashboard, Int) -> Unit) {
        viewModelScope.launch {
            taskRepository.getTasksCount(projectId, null, null).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        // toodo = 0%, ongoing = 50%, done = 100%
                        val counting = response.data

                        val todo = counting.totalTaskTodo * TODO_WEIGHT_PERCENT
                        val onGoing = counting.totalTaskOnGoing * ONGOING_WEIGHT_PERCENT
                        val done = counting.totalTaskDone * DONE_WEIGHT_PERCENT
                        var progress =
                            (todo + onGoing + done) / (counting.totalTaskTodo + counting.totalTaskOnGoing + counting.totalTaskDone)
                        if (progress > 100) {
                            progress = 100
                        }

                        result.invoke(counting, progress)
                    }

                    is Resource.Failure -> {}
                }
            }
        }
    }
}