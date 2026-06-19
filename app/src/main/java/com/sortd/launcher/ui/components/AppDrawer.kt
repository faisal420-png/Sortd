package com.sortd.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.sortd.launcher.domain.model.AppModel
import com.sortd.launcher.ui.viewmodel.AppDrawerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    viewModel: AppDrawerViewModel,
    onDismiss: () -> Unit,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showContextMenu by remember { mutableStateOf<String?>(null) }

    // Group apps by first letter for sticky headers
    val groupedApps = remember(filteredApps) {
        filteredApps.groupBy { app ->
            app.appName.firstOrNull()?.uppercase() ?: "#"
        }
    }

    val sortedLetters = remember(groupedApps) {
        groupedApps.keys.sorted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search apps...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            sortedLetters.forEach { letter ->
                val appsForLetter = groupedApps[letter] ?: emptyList()

                // Sticky header
                item(key = "header_$letter") {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }

                // App items for this letter
                items(appsForLetter, key = { it.packageName }) { app ->
                    AppDrawerItem(
                        app = app,
                        onAppClick = {
                            onAppClick(app.packageName)
                            onDismiss()
                        },
                        onLongClick = { showContextMenu = app.packageName },
                        showContextMenu = showContextMenu == app.packageName,
                        onDismissContextMenu = { showContextMenu = null },
                        onUninstall = {
                            viewModel.uninstallApp(app.packageName)
                            showContextMenu = null
                        },
                        onAppInfo = {
                            viewModel.openAppInfo(app.packageName)
                            showContextMenu = null
                        },
                        onAddToHome = {
                            viewModel.addFavorite(app.packageName)
                            showContextMenu = null
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerItem(
    app: AppModel,
    onAppClick: () -> Unit,
    onLongClick: () -> Unit,
    showContextMenu: Boolean,
    onDismissContextMenu: () -> Unit,
    onUninstall: () -> Unit,
    onAppInfo: () -> Unit,
    onAddToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onAppClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            if (app.icon != null) {
                androidx.compose.foundation.Image(
                    bitmap = app.icon.toBitmap().asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.appName.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (showContextMenu) {
                Box {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = onDismissContextMenu
                    ) {
                        DropdownMenuItem(
                            text = { Text("Uninstall") },
                            onClick = onUninstall,
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("App Info") },
                            onClick = onAppInfo,
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Add to Home Screen") },
                            onClick = onAddToHome,
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                        )
                    }
                }
            } else {
                IconButton(onClick = onLongClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}