package com.sortd.launcher.data.repository

import com.sortd.launcher.data.local.dao.TaskDao
import com.sortd.launcher.data.local.entity.TaskEntity
import com.sortd.launcher.domain.model.Task
import com.sortd.launcher.domain.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.updateCompletionStatus(taskId, isCompleted, completedAt)
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    private fun TaskEntity.toDomain() = Task(
        id = id,
        title = title,
        description = description,
        priority = TaskPriority.fromValue(priority),
        isCompleted = isCompleted,
        createdAt = createdAt,
        completedAt = completedAt
    )

    private fun Task.toEntity() = TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority.value,
        isCompleted = isCompleted,
        createdAt = createdAt,
        completedAt = completedAt
    )
}