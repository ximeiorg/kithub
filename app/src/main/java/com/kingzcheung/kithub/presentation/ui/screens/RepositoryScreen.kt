package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.Branch
import com.kingzcheung.kithub.domain.model.Content
import com.kingzcheung.kithub.domain.model.ContentType
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.RepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryScreen(
    owner: String,
    repoName: String,
    onNavigateToIssue: (Int) -> Unit,
    onNavigateToPullRequest: (Int) -> Unit,
    onNavigateToCommit: (String) -> Unit,
    onNavigateToUser: (String) -> Unit = {},
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showBranchSelector by remember { mutableStateOf(false) }
    
    state.repository?.let { repo ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(repo.fullName) },
                    actions = {
                        IconButton(onClick = { viewModel.toggleStar() }) {
                            Icon(
                                imageVector = if (state.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star"
                            )
                        }
                        IconButton(onClick = { showBranchSelector = true }) {
                            Icon(Icons.Default.Source, contentDescription = "Branches")
                        }
                    }
                )
            }
        ) { padding ->
            if (state.loading && state.contents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.clickable { onNavigateToUser(repo.owner.login) }
                                    ) {
                                        UserAvatar(
                                            avatarUrl = repo.owner.avatarUrl,
                                            size = 48
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = repo.fullName,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        if (repo.description != null) {
                                            Text(
                                                text = repo.description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    IconText(
                                        icon = Icons.Default.Star,
                                        text = "${repo.stargazersCount} stars"
                                    )
                                    IconText(
                                        icon = Icons.Default.CallSplit,
                                        text = "${repo.forksCount} forks"
                                    )
                                    IconText(
                                        icon = Icons.Default.ErrorOutline,
                                        text = "${repo.openIssuesCount} issues"
                                    )
                                }
                                
                                repo.language?.let { lang ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LanguageBadge(language = lang)
                                }
                                
                                repo.license?.let { license ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    IconText(
                                        icon = Icons.Default.Gavel,
                                        text = license.name
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("Code") }
                            )
                            FilterChip(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1; viewModel.loadIssues() },
                                label = { Text("Issues (${repo.openIssuesCount})") }
                            )
                            FilterChip(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2; viewModel.loadPullRequests() },
                                label = { Text("PRs") }
                            )
                            FilterChip(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3; viewModel.loadCommits() },
                                label = { Text("Commits") }
                            )
                        }
                    }
                    
                    when (selectedTab) {
                        0 -> {
                            if (state.currentPath.isNotEmpty()) {
                                item {
                                    TextButton(onClick = { viewModel.navigateToPath("") }) {
                                        Icon(Icons.Default.Home, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Root")
                                    }
                                }
                            }
                            
                            items(state.contents, key = { it.sha }) { content ->
                                ContentItem(
                                    content = content,
                                    onClick = {
                                        if (content.type == ContentType.DIR) {
                                            viewModel.navigateToPath(content.path)
                                        }
                                    }
                                )
                            }
                        }
                        1 -> {
                            items(state.issues, key = { it.id }) { issue ->
                                IssueCard(
                                    issue = issue,
                                    onClick = { onNavigateToIssue(issue.number) }
                                )
                            }
                        }
                        2 -> {
                            items(state.pullRequests, key = { it.id }) { pr ->
                                PullRequestCard(
                                    pr = pr,
                                    onClick = { onNavigateToPullRequest(pr.number) }
                                )
                            }
                        }
                        3 -> {
                            items(state.commits, key = { it.sha }) { commit ->
                                CommitCard(
                                    commit = commit,
                                    onClick = { onNavigateToCommit(commit.sha) }
                                )
                            }
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
                        modifier = Modifier.fillMaxWidth(),
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