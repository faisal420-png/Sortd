package com.sortd.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.sortd.launcher.ui.MainScreen
import com.sortd.launcher.ui.theme.SortdTheme
import com.sortd.launcher.ui.viewmodel.AppDrawerViewModel
import com.sortd.launcher.ui.viewmodel.MainViewModel
import com.sortd.launcher.ui.viewmodel.NoteViewModel
import com.sortd.launcher.ui.viewmodel.TaskViewModel
import com.sortd.launcher.data.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val preferences by mainViewModel.preferences.collectAsState()

            SortdTheme(
                darkTheme = when (preferences.isDarkTheme) {
                    true -> true
                    false -> false
                    null -> androidx.compose.foundation.isSystemInDarkTheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SortdLauncherApp(
                        onAppLaunch = { packageName ->
                            appRepository.launchApp(packageName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SortdLauncherApp(
    onAppLaunch: (String) -> Unit = {}
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val taskViewModel: TaskViewModel = hiltViewModel()
    val noteViewModel: NoteViewModel = hiltViewModel()
    val appDrawerViewModel: AppDrawerViewModel = hiltViewModel()

    MainScreen(
        mainViewModel = mainViewModel,
        taskViewModel = taskViewModel,
        noteViewModel = noteViewModel,
        appDrawerViewModel = appDrawerViewModel,
        onAppLaunch = onAppLaunch
    )
}