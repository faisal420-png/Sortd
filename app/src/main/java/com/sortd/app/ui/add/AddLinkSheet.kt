package com.sortd.app.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkSheet(
    initialUrl: String = "",
    onDismiss: () -> Unit,
    vm: AddLinkViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val folders by vm.folders.collectAsState()
    var url by remember { mutableStateOf(initialUrl) }
    var selectedFolder by remember { mutableStateOf<Long?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScopeSafe()

    LaunchedEffect(state.done) {
        if (state.done) {
            scope.launch {
                sheetState.hide()
                vm.reset()
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Save a link", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                placeholder = { Text("https://…") },
                leadingIcon = { Icon(Icons.Default.Link, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            if (folders.isNotEmpty()) {
                Text(
                    "Folder",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedFolder == null,
                            onClick = { selectedFolder = null },
                            label = { Text("None") }
                        )
                    }
                    items(items = folders, key = { it.id }) { folder ->
                        FilterChip(
                            selected = selectedFolder == folder.id,
                            onClick = { selectedFolder = folder.id },
                            label = { Text(folder.name) },
                            leadingIcon = { Icon(Icons.Default.Folder, null) }
                        )
                    }
                }
            }

            state.error?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { vm.save(url, selectedFolder) },
                enabled = !state.isSaving && url.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Saving…", modifier = Modifier.padding(start = 8.dp))
                    }
                } else {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun rememberCoroutineScopeSafe() = androidx.compose.runtime.rememberCoroutineScope()
