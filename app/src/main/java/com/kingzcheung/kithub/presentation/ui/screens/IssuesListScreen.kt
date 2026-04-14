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
import com.kingzcheung.kithub.presentation.ui.components.IssueCard
import com.kingzcheung.kithub.presentation.viewmodel.IssuesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesListScreen(
    onNavigateToIssue: (String, String, Int) -> Unit,
    viewModel: IssuesListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Issues") },
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
            FilterChipsRow(
                stateFilter = state.stateFilter,
                onStateFilterChange = { viewModel.setStateFilter(it) }
            )
            
            if (state.loading && state.issues.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error loading issues",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            } else if (state.issues.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No issues",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No issues found",
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
                        items = state.issues,
                        key = { "issue_${it.id}_${it.number}" }
                    ) { issue ->
                        val repoInfo = extractRepoInfo(issue.repositoryUrl)
                        IssueCard(
                            issue = issue,
                            onClick = {
                                if (repoInfo != null) {
                                    onNavigateToIssue(repoInfo.first, repoInfo.second, issue.number)
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
            FilterDialog(
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
fun FilterChipsRow(
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
fun FilterDialog(
    currentFilter: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(currentFilter) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Issues") },
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

fun extractRepoInfo(repositoryUrl: String?): Pair<String, String>? {
    if (repositoryUrl == null) return null
    val pattern = "repos/(.+)/(.+)".toRegex()
    val match = pattern.find(repositoryUrl)
    return if (match != null) {
        Pair(match.groupValues[1], match.groupValues[2])
    } else null
}