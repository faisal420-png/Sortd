package com.sortd.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sortd.app.ui.add.AddLinkSheet
import com.sortd.app.ui.folders.FolderDetailScreen
import com.sortd.app.ui.folders.FoldersScreen
import com.sortd.app.ui.home.HomeScreen
import com.sortd.app.ui.nav.Route
import com.sortd.app.ui.settings.SettingsScreen

private data class BottomTab(val route: Route, val label: String, val icon: ImageVector)

private val tabs = listOf(
    BottomTab(Route.Home, "Home", Icons.Default.Home),
    BottomTab(Route.Folders, "Folders", Icons.Default.Folder),
    BottomTab(Route.Settings, "Settings", Icons.Default.Settings),
)

@Composable
fun MainScreen(sharedUrl: String? = null) {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var showAddSheet by remember { mutableStateOf(sharedUrl != null) }
    var prefilledUrl by remember { mutableStateOf(sharedUrl.orEmpty()) }

    Scaffold(
        bottomBar = {
            if (currentRoute in tabs.map { it.route.path }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route.path,
                            onClick = {
                                nav.navigate(tab.route.path) {
                                    popUpTo(Route.Home.path) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Route.Home.path || currentRoute == Route.Folders.path) {
                FloatingActionButton(onClick = {
                    prefilledUrl = ""
                    showAddSheet = true
                }) {
                    Icon(Icons.Default.Add, "Add link")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Home.path) {
                HomeScreen(onAddLink = {
                    prefilledUrl = ""
                    showAddSheet = true
                })
            }
            composable(Route.Folders.path) {
                FoldersScreen(onOpenFolder = { id ->
                    nav.navigate(Route.FolderDetail.create(id))
                })
            }
            composable(
                route = Route.FolderDetail.path,
                arguments = listOf(navArgument(Route.FolderDetail.ARG_ID) { type = NavType.StringType })
            ) {
                FolderDetailScreen(onBack = { nav.popBackStack() })
            }
            composable(Route.Settings.path) { SettingsScreen() }
        }
    }

    if (showAddSheet) {
        AddLinkSheet(
            initialUrl = prefilledUrl,
            onDismiss = { showAddSheet = false }
        )
    }
}
