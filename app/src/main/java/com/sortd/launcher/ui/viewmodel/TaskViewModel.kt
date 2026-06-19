package com.sortd.launcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.launcher.data.repository.TaskRepository
import com.sortd.launcher.domain.model.Task
import com.sortd.launcher.domain.model.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = taskRepository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _taskInput = MutableStateFlow("")
    val taskInput: StateFlow<String> = _taskInput.asStateFlow()

    private val _selectedPriority = MutableStateFlow(TaskPriority.MEDIUM)
    val selectedPriority: StateFlow<TaskPriority> = _selectedPriority.asStateFlow()

    fun setTaskInput(input: String) {
        _taskInput.value = input
    }

    fun setPriority(priority: TaskPriority) {
        _selectedPriority.value = priority
    }

    fun addTask() {
        val title = _taskInput.value.trim()
        if (title.isEmpty()) return
        viewModelScope.launch {
            taskRepository.addTask(
                Task(title = title, priority = _selectedPriority.value)
            )
            _taskInput.value = ""
        }
    }

    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId, !isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch {
            taskRepository.deleteAllTasks()
        }
    }
}