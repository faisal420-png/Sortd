package com.sortd.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sortd.app.data.SavedLink
import com.sortd.app.ui.components.EmptyState
import com.sortd.app.ui.components.LinkGridCard
import com.sortd.app.ui.components.LinkListRow

@Composable
fun HomeScreen(
    onAddLink: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val links by vm.links.collectAsState()
    val query by vm.query.collectAsState()
    val filter by vm.filter.collectAsState()
    val grid by vm.gridView.collectAsState()
    var pendingDelete by remember { mutableStateOf<SavedLink?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sortd",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = vm::toggleGrid) {
                Icon(
                    if (grid) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = "Toggle layout"
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = vm::setQuery,
            placeholder = { Text("Search saves…") },
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter == HomeFilter.ALL,
                onClick = { vm.setFilter(HomeFilter.ALL) },
                label = { Text("All") },
                leadingIcon = { Icon(Icons.Default.Inbox, null) }
            )
            FilterChip(
                selected = filter == HomeFilter.FAVORITES,
                onClick = { vm.setFilter(HomeFilter.FAVORITES) },
                label = { Text("Favorites") },
                leadingIcon = { Icon(Icons.Outlined.FavoriteBorder, null) }
            )
        }

        if (links.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Inbox,
                title = if (query.isNotBlank()) "No matches" else "No saves yet",
                subtitle = if (query.isNotBlank()) "Try a different search."
                else "Tap + to paste a link, or share to Sortd from any app."
            )
        } else {
            if (grid) {
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
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items = links, key = { it.id }) { link ->
                        LinkListRow(
                            link = link,
                            onLongPress = { pendingDelete = link },
                            onFavorite = { vm.toggleFavorite(link) }
                        )
                    }
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
