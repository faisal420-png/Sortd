package com.sortd.launcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sortd.launcher.data.local.AppPreferences
import com.sortd.launcher.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val preferences: StateFlow<AppPreferences> = preferencesManager.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences())

    private val _currentPage = MutableStateFlow(1) // Start on center page
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun setShowTasks(show: Boolean) {
        viewModelScope.launch { preferencesManager.setShowTasks(show) }
    }

    fun setShowNotes(show: Boolean) {
        viewModelScope.launch { preferencesManager.setShowNotes(show) }
    }

    fun setDrawerColumns(columns: Int) {
        viewModelScope.launch { preferencesManager.setDrawerColumns(columns) }
    }
}