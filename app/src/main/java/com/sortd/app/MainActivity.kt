package com.sortd.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.SettingsRepository
import com.sortd.app.data.ThemeMode
import com.sortd.app.ui.MainScreen
import com.sortd.app.ui.theme.SortdTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedUrl = extractSharedUrl(intent)
        setContent {
            val themeVm: AppThemeViewModel = hiltViewModel()
            val theme by themeVm.theme.collectAsState()
            val dynamic by themeVm.dynamic.collectAsState()
            val dark = when (theme) {
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            SortdTheme(darkTheme = dark, dynamicColor = dynamic) {
                MainScreen(sharedUrl = sharedUrl)
            }
        }
    }

    private fun extractSharedUrl(intent: Intent?): String? {
        if (intent?.action != Intent.ACTION_SEND) return null
        val raw = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim() ?: return null
        val pattern = Regex("""https?://\S+""")
        return pattern.find(raw)?.value ?: raw.takeIf { it.isNotBlank() }
    }
}

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    settings: SettingsRepository
) : ViewModel() {
    val theme: StateFlow<ThemeMode> = settings.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)
    val dynamic: StateFlow<Boolean> = settings.dynamicColor
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
}
