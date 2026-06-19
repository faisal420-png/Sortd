package com.sortd.launcher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sortd.launcher.domain.model.Task
import com.sortd.launcher.domain.model.TaskPriority
import com.sortd.launcher.ui.theme.PriorityHigh
import com.sortd.launcher.ui.theme.PriorityLow
import com.sortd.launcher.ui.theme.PriorityMedium
import com.sortd.launcher.ui.viewmodel.TaskViewModel

@Composable
fun TaskPage(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val tasks by viewModel.tasks.collectAsState()
    val taskInput by viewModel.taskInput.collectAsState()
    val selectedPriority by viewModel.selectedPriority.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Smart Tasks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            if (tasks.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearAllTasks() }) { Text("Clear All", color = MaterialTheme.colorScheme.error) }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TaskPriority.entries.forEach { priority ->
                FilterChip(selected = selectedPriority == priority, onClick = { viewModel.setPriority(priority) },
                    label = { Text(priority.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = when (priority) { TaskPriority.HIGH -> PriorityHigh.copy(alpha = 0.2f); TaskPriority.MEDIUM -> PriorityMedium.copy(alpha = 0.2f); TaskPriority.LOW -> PriorityLow.copy(alpha = 0.2f) }))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No tasks yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks, key = { it.id }) { task -> TaskItem(task = task, onToggle = { viewModel.toggleTask(task.id, task.isCompleted) }, onDelete = { viewModel.deleteTask(task) }) }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = taskInput, onValueChange = { viewModel.setTaskInput(it) }, placeholder = { Text("Add a task...") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                IconButton(onClick = { viewModel.addTask() }, enabled = taskInput.isNotBlank()) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Add", tint = if (taskInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                }
            },
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)))
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val priorityColor = when (task.priority) { TaskPriority.HIGH -> PriorityHigh; TaskPriority.MEDIUM -> PriorityMedium; TaskPriority.LOW -> PriorityLow }
    val bgColor by animateColorAsState(targetValue = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface)

    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(confirmValueChange = { value -> value == SwipeToDismissBoxValue.EndToStart.also { if (it) onDelete() } }),
        backgroundContent = {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp), contentAlignment = Alignment.CenterEnd) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = bgColor, tonalElevation = 1.dp) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(4.dp).height(32.dp).clip(RoundedCornerShape(2.dp)).background(priorityColor))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = task.title, style = MaterialTheme.typography.bodyLarge, fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}