package com.sortd.launcher.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sortd.launcher.ui.components.*
import com.sortd.launcher.ui.viewmodel.AppDrawerViewModel
import com.sortd.launcher.ui.viewmodel.MainViewModel
import com.sortd.launcher.ui.viewmodel.NoteViewModel
import com.sortd.launcher.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    taskViewModel: TaskViewModel,
    noteViewModel: NoteViewModel,
    appDrawerViewModel: AppDrawerViewModel,
    onAppLaunch: (String) -> Unit
) {
    val preferences by mainViewModel.preferences.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showAppDrawer by remember { mutableStateOf(false) }
    var showAppSelectDialog by remember { mutableStateOf(false) }

    val pages = remember(preferences.showTasksPage, preferences.showNotesPage) {
        buildList {
            if (preferences.showTasksPage) add("tasks")
            add("home")
            if (preferences.showNotesPage) add("notes")
        }
    }

    val pagerState = rememberPagerState(
        initialPage = pages.indexOf("home").coerceAtLeast(0),
        pageCount = { pages.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        mainViewModel.setCurrentPage(pagerState.currentPage)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            SortdTopBar(onSettingsClick = { showSettings = true })

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { pageIndex ->
                when (pages.getOrElse(pageIndex) { "home" }) {
                    "tasks" -> TaskPage(viewModel = taskViewModel)
                    "notes" -> NotesPage(viewModel = noteViewModel)
                    else -> FavoritesPage(
                        viewModel = appDrawerViewModel,
                        onAppClick = onAppLaunch,
                        onAddFavorite = { showAppSelectDialog = true }
                    )
                }
            }

            BottomDock(onAppClick = onAppLaunch, onDrawerOpen = { showAppDrawer = true })
        }
    }

    if (showSettings) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            SettingsScreen(viewModel = mainViewModel, taskViewModel = taskViewModel, noteViewModel = noteViewModel, onDismiss = { showSettings = false })
        }
    }

    if (showAppDrawer) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AppDrawer(viewModel = appDrawerViewModel, onDismiss = { showAppDrawer = false },
                onAppClick = { p -> onAppLaunch(p); showAppDrawer = false }
            )
        }
    }

    if (showAppSelectDialog) {
        AppSelectDialog(viewModel = appDrawerViewModel, onDismiss = { showAppSelectDialog = false },
            onAppSelected = { appDrawerViewModel.addFavorite(it) }
        )
    }
}