package com.sortd.app.ui.folders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.sortd.app.data.SavedLink
import com.sortd.app.ui.components.EmptyState
import com.sortd.app.ui.components.LinkGridCard

@Composable
fun FolderDetailScreen(
    onBack: () -> Unit,
    vm: FolderDetailViewModel = hiltViewModel()
) {
    val folder by vm.folder.collectAsState()
    val links by vm.links.collectAsState()
    var pendingDelete by remember { mutableStateOf<SavedLink?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(parseFolderColor(folder?.colorHex)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White)
            }
            Text(
                text = folder?.name ?: "Folder",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }

        if (links.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Folder,
                title = "Folder is empty",
                subtitle = "Add saves to this folder when creating or editing them."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = links, key = { it.id }) { link ->
                    LinkGridCard(
                        link = link,
                        onLongPress = { pendingDelete = link },
                        onFavorite = { vm.toggleFavorite(link) }
                    )
                }
            }
        }
    }

    pendingDelete?.let { link ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete save?") },
            text = { Text(link.title ?: link.url) },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(link.id)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

private fun parseFolderColor(hex: String?): Color = hex?.let {
    runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull()
} ?: Color(0xFF7C4DFF)
