package com.sortd.app.ui.folders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sortd.app.data.FolderWithCount
import com.sortd.app.ui.components.EmptyState

private val FOLDER_COLORS = listOf(
    "#7C4DFF", "#E91E63", "#03A9F4", "#4CAF50",
    "#FF9800", "#F44336", "#009688", "#9C27B0"
)

@Composable
fun FoldersScreen(
    onOpenFolder: (Long) -> Unit,
    vm: FoldersViewModel = hiltViewModel()
) {
    val folders by vm.folders.collectAsState()
    var showCreate by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<FolderWithCount?>(null) }
    var deleteTarget by remember { mutableStateOf<FolderWithCount?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Folders",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, "Create folder")
            }
        }

        if (folders.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Folder,
                title = "No folders",
                subtitle = "Create a folder to group your saves."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = folders, key = { it.id }) { folder ->
                    FolderRow(
                        folder = folder,
                        onClick = { onOpenFolder(folder.id) },
                        onRename = { renameTarget = folder },
                        onDelete = { deleteTarget = folder }
                    )
                }
            }
        }
    }

    if (showCreate) {
        FolderEditDialog(
            title = "New folder",
            initialName = "",
            initialColor = FOLDER_COLORS.first(),
            onDismiss = { showCreate = false },
            onConfirm = { name, color ->
                vm.createFolder(name, color)
                showCreate = false
            }
        )
    }

    renameTarget?.let { folder ->
        FolderEditDialog(
            title = "Rename folder",
            initialName = folder.name,
            initialColor = folder.colorHex,
            onDismiss = { renameTarget = null },
            onConfirm = { name, _ ->
                vm.rename(folder.id, name)
                renameTarget = null
            }
        )
    }

    deleteTarget?.let { folder ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete folder?") },
            text = { Text("Saves in \"${folder.name}\" stay in your library.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(folder.id)
                    deleteTarget = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun FolderRow(
    folder: FolderWithCount,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(parseColor(folder.colorHex)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = folder.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${folder.itemCount} ${if (folder.itemCount == 1) "save" else "saves"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Default.MoreVert, "More")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = { menuOpen = false; onRename() }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = { menuOpen = false; onDelete() }
                    )
                }
            }
        }
    }
}

@Composable
private fun FolderEditDialog(
    title: String,
    initialName: String,
    initialColor: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var color by remember { mutableStateOf(initialColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Folder name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FOLDER_COLORS.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(parseColor(hex))
                                .clickable { color = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == hex) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, color) },
                enabled = name.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun parseColor(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrDefault(Color(0xFF7C4DFF))
