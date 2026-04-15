package com.kingzcheung.kithub.presentation.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.Branch
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.RepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryScreen(
    owner: String,
    repoName: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToIssues: () -> Unit = {},
    onNavigateToPullRequests: () -> Unit = {},
    onNavigateToCommits: () -> Unit = {},
    onNavigateToUser: (String) -> Unit = {},
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showBranchSelector by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.repository != null) {
                        IconButton(onClick = { /* TODO: Add new issue */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Issue")
                        }
                        IconButton(onClick = { viewModel.toggleStar() }) {
                            Icon(
                                imageVector = if (state.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star"
                            )
                        }
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.loading && state.repository == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && state.repository == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadRepository() }) {
                        Text("Retry")
                    }
                }
            }
        } else if (state.repository != null) {
            val repo = state.repository!!
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.clickable { onNavigateToUser(repo.owner.login) }
                                ) {
                                    UserAvatar(
                                        avatarUrl = repo.owner.avatarUrl,
                                        size = 40
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = repo.fullName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (repo.description != null) {
                                        Text(
                                            text = repo.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconText(
                                    icon = Icons.Default.Star,
                                    text = "${repo.stargazersCount}",
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                                IconText(
                                    icon = Icons.Default.CallSplit,
                                    text = "${repo.forksCount}",
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                                IconText(
                                    icon = Icons.Default.ErrorOutline,
                                    text = "${repo.openIssuesCount}",
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                            }
                            
                            repo.language?.let { lang ->
                                Spacer(modifier = Modifier.height(6.dp))
                                LanguageBadge(language = lang)
                            }
                            
                            repo.license?.let { license ->
                                Spacer(modifier = Modifier.height(6.dp))
                                IconText(
                                    icon = Icons.Default.Gavel,
                                    text = license.name,
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.ErrorOutline,
                        title = "Issues",
                        count = repo.openIssuesCount,
                        onClick = onNavigateToIssues
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.CallSplit,
                        title = "Pull Requests",
                        onClick = onNavigateToPullRequests
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.PlayArrow,
                        title = "Actions",
                        onClick = { /* TODO */ }
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.MenuBook,
                        title = "Wiki",
                        onClick = { /* TODO */ }
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.People,
                        title = "Contributors",
                        onClick = { /* TODO */ }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { showBranchSelector = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Source,
                            contentDescription = "Branch",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = state.selectedBranch,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.Folder,
                        title = "Code",
                        onClick = { /* Navigate to code browser */ }
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.History,
                        title = "Commits",
                        onClick = onNavigateToCommits
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (state.readme != null) {
                    item {
                        Text(
                            text = "README",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                    
                    item {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    settings.javaScriptEnabled = true
                                    loadDataWithBaseURL(
                                        null,
                                        generateMarkdownHtml(state.readme!!),
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
    
    if (showBranchSelector) {
        BranchSelectorDialog(
            branches = state.branches,
            selectedBranch = state.selectedBranch,
            onBranchSelected = { branch ->
                viewModel.selectBranch(branch)
                showBranchSelector = false
            },
            onDismiss = { showBranchSelector = false }
        )
    }
}

@Composable
fun RepositoryMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    count: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (count != null && count > 0) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BranchSelectorDialog(
    branches: List<Branch>,
    selectedBranch: String,
    onBranchSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Branch") },
        text = {
            LazyColumn {
                items(branches, key = { it.name }) { branch ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBranchSelected(branch.name) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = branch.name == selectedBranch,
                            onClick = { onBranchSelected(branch.name) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(branch.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}