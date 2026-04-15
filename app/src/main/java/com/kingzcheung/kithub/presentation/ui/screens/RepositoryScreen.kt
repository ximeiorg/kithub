package com.kingzcheung.kithub.presentation.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.Branch
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.ui.components.MarkwonText
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
    onNavigateToCode: () -> Unit = {},
    onNavigateToUser: (String) -> Unit = {},
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showBranchSelector by remember { mutableStateOf(false) }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (state.loading && state.repository == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && state.repository == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    FilledTonalButton(onClick = { viewModel.loadRepository() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry")
                    }
                }
            }
        } else if (state.repository != null) {
            val repo = state.repository!!
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
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
                }
                
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        onClick = { showBranchSelector = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Source,
                                contentDescription = "Branch",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.selectedBranch,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.UnfoldMore,
                                contentDescription = "Change branch",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.Folder,
                        title = "Code",
                        onClick = onNavigateToCode
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.History,
                        title = "Commits",
                        onClick = onNavigateToCommits
                    )
                }
                
                if (state.readme != null) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "README",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    item {
                        MarkwonText(
                            markdown = state.readme!!,
                            owner = state.repository!!.owner.login,
                            repo = state.repository!!.name,
                            branch = state.selectedBranch,
                            modifier = Modifier.padding(horizontal = 16.dp)
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
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (count != null && count > 0) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
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
        title = { 
            Text(
                text = "Select Branch",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            LazyColumn {
                items(branches, key = { it.name }) { branch ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBranchSelected(branch.name) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (branch.name == selectedBranch) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = branch.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Spacer(modifier = Modifier.width(28.dp))
                            Text(
                                text = branch.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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