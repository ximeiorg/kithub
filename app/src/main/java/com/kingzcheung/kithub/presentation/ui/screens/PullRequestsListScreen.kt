package com.kingzcheung.kithub.presentation.ui.screens

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
import com.kingzcheung.kithub.domain.model.PullRequest
import com.kingzcheung.kithub.domain.model.PullRequestBranch
import com.kingzcheung.kithub.presentation.ui.components.formatRelativeTime
import com.kingzcheung.kithub.presentation.viewmodel.PullRequestsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRequestsListScreen(
    onNavigateToPullRequest: (String, String, Int) -> Unit,
    viewModel: PullRequestsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pull Requests") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrFilterChipsRow(
                stateFilter = state.stateFilter,
                onStateFilterChange = { viewModel.setStateFilter(it) }
            )
            
            if (state.loading && state.pullRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.pullRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No pull requests",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No pull requests found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.pullRequests,
                        key = { "pr_${it.id}_${it.number}" }
                    ) { pr ->
                        PrListItem(
                            pullRequest = pr,
                            onClick = {
                                val repoInfo = extractRepoInfoFromPullRequest(pr)
                                if (repoInfo != null) {
                                    onNavigateToPullRequest(repoInfo.first, repoInfo.second, pr.number)
                                }
                            }
                        )
                    }
                    
                    if (state.hasMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.loading) {
                                    CircularProgressIndicator()
                                } else {
                                    Button(onClick = { viewModel.loadMore() }) {
                                        Text("Load More")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showFilterDialog) {
            PrFilterDialog(
                currentFilter = state.stateFilter,
                onDismiss = { showFilterDialog = false },
                onApply = { filter ->
                    viewModel.setStateFilter(filter)
                    showFilterDialog = false
                }
            )
        }
    }
}

@Composable
fun PrFilterChipsRow(
    stateFilter: String,
    onStateFilterChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = stateFilter == "all",
            onClick = { onStateFilterChange("all") },
            label = { Text("All") }
        )
        FilterChip(
            selected = stateFilter == "open",
            onClick = { onStateFilterChange("open") },
            label = { Text("Open") }
        )
        FilterChip(
            selected = stateFilter == "closed",
            onClick = { onStateFilterChange("closed") },
            label = { Text("Closed") }
        )
    }
}

@Composable
fun PrFilterDialog(
    currentFilter: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(currentFilter) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Pull Requests") },
        text = {
            Column {
                Text("State", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" }
                    )
                    Text("All")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == "open",
                        onClick = { selectedFilter = "open" }
                    )
                    Text("Open")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == "closed",
                        onClick = { selectedFilter = "closed" }
                    )
                    Text("Closed")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApply(selectedFilter) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrListItem(
    pullRequest: PullRequest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#${pullRequest.number}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    color = when (pullRequest.state.name.lowercase()) {
                        "open" -> MaterialTheme.colorScheme.primaryContainer
                        "closed" -> if (pullRequest.merged) MaterialTheme.colorScheme.secondaryContainer
                                     else MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (pullRequest.state.name.lowercase()) {
                            "open" -> "Open"
                            "closed" -> if (pullRequest.merged) "Merged" else "Closed"
                            else -> pullRequest.state.name
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = pullRequest.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pullRequest.user.login,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatRelativeTime(pullRequest.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (pullRequest.draft) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Draft",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (pullRequest.base.ref.isNotEmpty() || pullRequest.head.ref.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pullRequest.base.ref,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " ← ",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = pullRequest.head.ref,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun extractRepoInfoFromPullRequest(pr: PullRequest): Pair<String, String>? {
    val url = pr.repositoryUrl ?: pr.url
    val pattern = "repos/(.+)/(.+)".toRegex()
    val match = pattern.find(url)
    return if (match != null) {
        Pair(match.groupValues[1], match.groupValues[2])
    } else null
}