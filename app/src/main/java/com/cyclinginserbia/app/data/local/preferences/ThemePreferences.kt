package com.cyclinginserbia.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromName(name: String?): ThemeMode = when (name) {
            LIGHT.name -> LIGHT
            DARK.name -> DARK
            else -> SYSTEM
        }
    }
}

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.themeDataStore

    val themeMode: Flow<ThemeMode> = store.data.map { ThemeMode.fromName(it[KEY_THEME_MODE]) }

    suspend fun setThemeMode(mode: ThemeMode) {
        store.edit { it[KEY_THEME_MODE] = mode.name }
    }

    private companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
