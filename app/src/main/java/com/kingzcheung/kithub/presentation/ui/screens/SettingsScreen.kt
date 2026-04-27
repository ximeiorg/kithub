package com.kingzcheung.kithub.presentation.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.data.store.AppLanguage
import com.kingzcheung.kithub.data.store.ThemeMode
import com.kingzcheung.kithub.presentation.theme.ThemeColor
import com.kingzcheung.kithub.presentation.viewmodel.AppSettings
import com.kingzcheung.kithub.presentation.viewmodel.AuthViewModel
import com.kingzcheung.kithub.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showThemeColorDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCodeOptionsDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SettingsSectionHeader(title = "General")
            }
            
            item {
                SettingsGroupCard {
                    SettingsItemClickable(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = getThemeDisplayText(settings.themeMode),
                        onClick = { showThemeDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemClickable(
                        icon = Icons.Default.ColorLens,
                        title = "Theme Color",
                        subtitle = settings.themeColor.displayName,
                        onClick = { showThemeColorDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemClickable(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = getLanguageDisplayText(settings.appLanguage),
                        onClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemClickable(
                        icon = Icons.Default.Code,
                        title = "Code Options",
                        subtitle = "Line numbers, word wrap, theme",
                        onClick = { showCodeOptionsDialog = true }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "Account")
            }
            
            item {
                SettingsGroupCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Account")
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GitHub Account",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Logged in",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "About")
            }
            
            item {
                SettingsGroupCard {
                    SettingsItemClickable(
                        icon = Icons.Default.Code,
                        title = "Source Code",
                        subtitle = "View on GitHub",
                        onClick = { openUrl(context, "https://github.com/kingzcheung/kithub") }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemClickable(
                        icon = Icons.Default.BugReport,
                        title = "Report Bug",
                        subtitle = "Submit an issue",
                        onClick = { openUrl(context, "https://github.com/kingzcheung/kithub/issues") }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemClickable(
                        icon = Icons.Default.Info,
                        title = "GitHub API Docs",
                        subtitle = "REST API documentation",
                        onClick = { openUrl(context, "https://docs.github.com/en/rest") }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout from your GitHub account?") },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                        showLogoutDialog = false
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = settings.themeMode,
            onThemeSelected = { mode ->
                settingsViewModel.setThemeMode(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = settings.appLanguage,
            onLanguageSelected = { lang ->
                settingsViewModel.setAppLanguage(lang)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
    
    if (showCodeOptionsDialog) {
        CodeOptionsDialog(
            currentSettings = settings,
            onWrapChanged = { settingsViewModel.setCodeWrap(it) },
            onLineNumbersChanged = { settingsViewModel.setCodeLineNumbers(it) },
            onDismiss = { showCodeOptionsDialog = false }
        )
    }
    
    if (showThemeColorDialog) {
        ThemeColorSelectionDialog(
            currentColor = settings.themeColor,
            onColorSelected = { color ->
                settingsViewModel.setThemeColor(color)
                showThemeColorDialog = false
            },
            onDismiss = { showThemeColorDialog = false }
        )
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsGroupCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItemClickable(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme") },
        text = {
            Column {
                ThemeMode.values().forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (mode == currentTheme),
                                onClick = { onThemeSelected(mode) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (mode == currentTheme),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = getThemeDisplayText(mode),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Language") },
        text = {
            Column {
                AppLanguage.values().forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (lang == currentLanguage),
                                onClick = { onLanguageSelected(lang) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (lang == currentLanguage),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = getLanguageDisplayText(lang),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun CodeOptionsDialog(
    currentSettings: AppSettings,
    onWrapChanged: (Boolean) -> Unit,
    onLineNumbersChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var wrap by remember { mutableStateOf(currentSettings.codeWrap) }
    var lineNumbers by remember { mutableStateOf(currentSettings.codeLineNumbers) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Code Options") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            wrap = !wrap
                            onWrapChanged(wrap)
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = wrap,
                        onCheckedChange = {
                            wrap = it
                            onWrapChanged(it)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Word wrap",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            lineNumbers = !lineNumbers
                            onLineNumbersChanged(lineNumbers)
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = lineNumbers,
                        onCheckedChange = {
                            lineNumbers = it
                            onLineNumbersChanged(it)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Line numbers",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun ThemeColorSelectionDialog(
    currentColor: ThemeColor,
    onColorSelected: (ThemeColor) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme Color") },
        text = {
            Column {
                ThemeColor.values().forEach { color ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (color == currentColor),
                                onClick = { onColorSelected(color) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (color == currentColor),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        if (color != ThemeColor.DYNAMIC) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color.seedColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = color.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

fun getThemeDisplayText(mode: ThemeMode): String {
    return when (mode) {
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
        ThemeMode.SYSTEM -> "Follow system"
    }
}

fun getLanguageDisplayText(lang: AppLanguage): String {
    return when (lang) {
        AppLanguage.ENGLISH -> "English"
        AppLanguage.CHINESE -> "中文"
        AppLanguage.SYSTEM -> "Follow system"
    }
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}