package com.kingzcheung.kithub.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class ThemeColor(val displayName: String, val seedColor: androidx.compose.ui.graphics.Color) {
    DYNAMIC("Dynamic (Material You)", androidx.compose.ui.graphics.Color.Unspecified),
    GREEN("GitHub Green", androidx.compose.ui.graphics.Color(0xFF2EA043)),
    BLUE("Ocean Blue", androidx.compose.ui.graphics.Color(0xFF2563EB)),
    PURPLE("Purple", androidx.compose.ui.graphics.Color(0xFF7C3AED)),
    RED("Red", androidx.compose.ui.graphics.Color(0xFFDC2626)),
    TEAL("Teal", androidx.compose.ui.graphics.Color(0xFF0891B2)),
    ORANGE("Orange", androidx.compose.ui.graphics.Color(0xFFEA580C)),
    PINK("Pink", androidx.compose.ui.graphics.Color(0xFFDB2777))
}

enum class AppLanguage {
    ENGLISH,
    CHINESE,
    SYSTEM
}

class SettingsStore(private val context: Context) {
    
    private object Keys {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val THEME_COLOR = intPreferencesKey("theme_color")
        val APP_LANGUAGE = intPreferencesKey("app_language")
        val CODE_WRAP = booleanPreferencesKey("code_wrap")
        val CODE_LINE_NUMBERS = booleanPreferencesKey("code_line_numbers")
        val CODE_THEME = stringPreferencesKey("code_theme")
    }
    
    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data
        .map { preferences ->
            val mode = preferences[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.ordinal
            ThemeMode.values().getOrElse(mode) { ThemeMode.SYSTEM }
        }
    
    val themeColor: Flow<ThemeColor> = context.settingsDataStore.data
        .map { preferences ->
            val color = preferences[Keys.THEME_COLOR] ?: ThemeColor.GREEN.ordinal
            ThemeColor.values().getOrElse(color) { ThemeColor.GREEN }
        }
    
    val appLanguage: Flow<AppLanguage> = context.settingsDataStore.data
        .map { preferences ->
            val lang = preferences[Keys.APP_LANGUAGE] ?: AppLanguage.SYSTEM.ordinal
            AppLanguage.values().getOrElse(lang) { AppLanguage.SYSTEM }
        }
    
    val codeWrap: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.CODE_WRAP] ?: true }
    
    val codeLineNumbers: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.CODE_LINE_NUMBERS] ?: true }
    
    val codeTheme: Flow<String> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.CODE_THEME] ?: "github" }
    
    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.ordinal
        }
    }
    
    suspend fun setThemeColor(color: ThemeColor) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.THEME_COLOR] = color.ordinal
        }
    }
    
    suspend fun setAppLanguage(language: AppLanguage) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.APP_LANGUAGE] = language.ordinal
        }
    }
    
    suspend fun setCodeWrap(wrap: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CODE_WRAP] = wrap
        }
    }
    
    suspend fun setCodeLineNumbers(show: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CODE_LINE_NUMBERS] = show
        }
    }
    
    suspend fun setCodeTheme(theme: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CODE_THEME] = theme
        }
    }
}