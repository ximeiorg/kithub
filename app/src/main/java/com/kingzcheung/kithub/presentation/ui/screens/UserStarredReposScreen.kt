package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.UserStarredReposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStarredReposScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRepository: (String, String) -> Unit,
    viewModel: UserStarredReposViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    var showSortMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.getStarredTab(context)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = strings.getBack(context))
                    }
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = strings.getSort(context))
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.getRecentlyStarred(context)) },
                            onClick = {
                                viewModel.setSortBy("created")
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (state.sortBy == "created") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.getRecentlyUpdated(context)) },
                            onClick = {
                                viewModel.setSortBy("updated")
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (state.sortBy == "updated") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = strings.getRefresh(context))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.repos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            ErrorState(
                message = state.error ?: strings.getUnknown(context),
                onRetry = { viewModel.refresh() },
                modifier = Modifier.padding(paddingValues)
            )
        } else if (state.repos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = strings.getNoResults(context),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.getNoRepositoriesFound(context),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = strings.getStarredRepositoryFormat(context, state.repos.size),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(
                    items = state.repos,
                    key = { it.id }
                ) { repo ->
                    RepositoryCard(
                        repo = repo,
                        onClick = { onNavigateToRepository(repo.owner.login, repo.name) }
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
}