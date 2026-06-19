package com.sortd.launcher.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sortd_preferences")

data class AppPreferences(
    val isDarkTheme: Boolean? = null,
    val showTasksPage: Boolean = true,
    val showNotesPage: Boolean = true,
    val drawerColumnCount: Int = 4
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode") // "system", "light", "dark"
        val SHOW_TASKS = booleanPreferencesKey("show_tasks")
        val SHOW_NOTES = booleanPreferencesKey("show_notes")
        val DRAWER_COLUMNS = intPreferencesKey("drawer_columns")
    }

    val preferences: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            isDarkTheme = when (prefs[Keys.THEME_MODE]) {
                "light" -> false
                "dark" -> true
                else -> null
            },
            showTasksPage = prefs[Keys.SHOW_TASKS] ?: true,
            showNotesPage = prefs[Keys.SHOW_NOTES] ?: true,
            drawerColumnCount = prefs[Keys.DRAWER_COLUMNS] ?: 4
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setShowTasks(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_TASKS] = show }
    }

    suspend fun setShowNotes(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_NOTES] = show }
    }

    suspend fun setDrawerColumns(columns: Int) {
        context.dataStore.edit { it[Keys.DRAWER_COLUMNS] = columns }
    }
}