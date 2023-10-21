package com.schpro.project.presentation.detailProject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.schpro.project.core.base.BaseViewModel
import com.schpro.project.core.base.UiState
import com.schpro.project.core.extension.toState
import com.schpro.project.data.models.Task
import com.schpro.project.data.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailSprintViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseViewModel() {
    private val _saveTask = MutableLiveData<UiState<String>>()

    val saveTaskState: LiveData<UiState<String>>
        get() = _saveTask

    fun saveTask(task: Task) {
        _saveTask.value = UiState.Loading
        viewModelScope.launch {
            taskRepository.saveTask(task).run {
                _saveTask.value = toState("Task berhasil di simpan")
            }
        }
    }

    fun updateStatusTask() {

    }
}