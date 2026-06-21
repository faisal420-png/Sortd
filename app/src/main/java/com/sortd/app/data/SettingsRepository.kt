package com.sortd.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val Context.dataStore by preferencesDataStore(name = "sortd_prefs")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val GRID_KEY = booleanPreferencesKey("grid_view")
    private val DYNAMIC_KEY = booleanPreferencesKey("dynamic_color")

    val themeMode: Flow<ThemeMode> = ctx.dataStore.data.map {
        runCatching { ThemeMode.valueOf(it[THEME_KEY] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM)
    }

    val gridView: Flow<Boolean> = ctx.dataStore.data.map { it[GRID_KEY] ?: true }

    val dynamicColor: Flow<Boolean> = ctx.dataStore.data.map { it[DYNAMIC_KEY] ?: true }

    suspend fun setThemeMode(mode: ThemeMode) {
        ctx.dataStore.edit { it[THEME_KEY] = mode.name }
    }

    suspend fun setGridView(grid: Boolean) {
        ctx.dataStore.edit { it[GRID_KEY] = grid }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        ctx.dataStore.edit { it[DYNAMIC_KEY] = enabled }
    }
}
