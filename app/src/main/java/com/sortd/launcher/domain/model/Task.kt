package com.sortd.launcher.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

enum class TaskPriority(val value: String, val label: String) {
    HIGH("high", "High"),
    MEDIUM("medium", "Medium"),
    LOW("low", "Low");

    companion object {
        fun fromValue(value: String): TaskPriority {
            return entries.find { it.value == value } ?: MEDIUM
        }
    }
}