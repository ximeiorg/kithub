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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.presentation.ui.components.IssueCard
import com.kingzcheung.kithub.presentation.viewmodel.IssuesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesListScreen(
    onNavigateToIssue: (String, String, Int) -> Unit,
    viewModel: IssuesListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.getIssues(context)) },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = strings.getFilter(context))
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = strings.getRefresh(context))
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
                onStateFilterChange = { viewModel.setStateFilter(it) },
                strings = strings,
                context = context
            )
            
            if (state.loading && state.issues.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.issues.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = strings.getNoResults(context),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = context.getString(com.kingzcheung.kithub.R.string.no_issues),
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
                                        Text(strings.getLoadMore(context))
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
                },
                strings = strings,
                context = context
            )
        }
    }
}

@Composable
fun FilterChipsRow(
    stateFilter: String,
    onStateFilterChange: (String) -> Unit,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
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
            label = { Text(context.getString(com.kingzcheung.kithub.R.string.all)) }
        )
        FilterChip(
            selected = stateFilter == "open",
            onClick = { onStateFilterChange("open") },
            label = { Text(strings.getOpen(context)) }
        )
        FilterChip(
            selected = stateFilter == "closed",
            onClick = { onStateFilterChange("closed") },
            label = { Text(strings.getClosed(context)) }
        )
    }
}

@Composable
fun FilterDialog(
    currentFilter: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    var selectedFilter by remember { mutableStateOf(currentFilter) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.getFilterIssues(context)) },
        text = {
            Column {
                Text(strings.getState(context), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" }
                    )
                    Text(context.getString(com.kingzcheung.kithub.R.string.all))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == "open",
                        onClick = { selectedFilter = "open" }
                    )
                    Text(strings.getOpen(context))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFilter == "closed",
                        onClick = { selectedFilter = "closed" }
                    )
                    Text(strings.getClosed(context))
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApply(selectedFilter) }) {
                Text(strings.getApply(context))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.getCancel(context))
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