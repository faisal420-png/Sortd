package com.sortd.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.app.data.SettingsRepository
import com.sortd.app.data.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repo.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val dynamicColor: StateFlow<Boolean> = repo.dynamicColor
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { repo.setThemeMode(mode) }
    }

    fun setDynamic(enabled: Boolean) {
        viewModelScope.launch { repo.setDynamicColor(enabled) }
    }
}
