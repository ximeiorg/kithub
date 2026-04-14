package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.store.SettingsStore
import com.kingzcheung.kithub.data.store.ThemeMode
import com.kingzcheung.kithub.data.store.ThemeColor
import com.kingzcheung.kithub.data.store.AppLanguage
import com.kingzcheung.kithub.presentation.theme.ThemeColor as ThemeColorEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeColor: ThemeColorEnum = ThemeColorEnum.GREEN,
    val appLanguage: AppLanguage = AppLanguage.SYSTEM,
    val codeWrap: Boolean = true,
    val codeLineNumbers: Boolean = true,
    val codeTheme: String = "github"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore
) : ViewModel() {
    
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsStore.themeMode.collect { mode ->
                _settings.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            settingsStore.themeColor.collect { color ->
                _settings.update { it.copy(themeColor = ThemeColorEnum.values().getOrElse(color.ordinal) { ThemeColorEnum.GREEN }) }
            }
        }
        viewModelScope.launch {
            settingsStore.appLanguage.collect { lang ->
                _settings.update { it.copy(appLanguage = lang) }
            }
        }
        viewModelScope.launch {
            settingsStore.codeWrap.collect { wrap ->
                _settings.update { it.copy(codeWrap = wrap) }
            }
        }
        viewModelScope.launch {
            settingsStore.codeLineNumbers.collect { show ->
                _settings.update { it.copy(codeLineNumbers = show) }
            }
        }
        viewModelScope.launch {
            settingsStore.codeTheme.collect { theme ->
                _settings.update { it.copy(codeTheme = theme) }
            }
        }
    }
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsStore.setThemeMode(mode)
        }
    }
    
    fun setThemeColor(color: ThemeColorEnum) {
        viewModelScope.launch {
            settingsStore.setThemeColor(ThemeColor.values().getOrElse(color.ordinal) { ThemeColor.GREEN })
        }
    }
    
    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsStore.setAppLanguage(language)
        }
    }
    
    fun setCodeWrap(wrap: Boolean) {
        viewModelScope.launch {
            settingsStore.setCodeWrap(wrap)
        }
    }
    
    fun setCodeLineNumbers(show: Boolean) {
        viewModelScope.launch {
            settingsStore.setCodeLineNumbers(show)
        }
    }
    
    fun setCodeTheme(theme: String) {
        viewModelScope.launch {
            settingsStore.setCodeTheme(theme)
        }
    }
}