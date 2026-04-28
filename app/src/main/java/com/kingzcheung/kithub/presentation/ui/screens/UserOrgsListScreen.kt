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
import com.kingzcheung.kithub.domain.model.UserBrief
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.UserOrgsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOrgsListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUser: (String) -> Unit,
    viewModel: UserOrgsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.getOrganizations(context)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = strings.getBack(context))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = strings.getRefresh(context))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.orgs.isEmpty()) {
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
        } else if (state.orgs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = strings.getNoResults(context),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.getNoOrganizationsFound(context),
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
                        text = strings.getOrganizationFormat(context, state.orgs.size),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(
                    items = state.orgs,
                    key = { it.id }
                ) { org ->
                    OrgItem(
                        org = org,
                        onClick = { onNavigateToUser(org.login) },
                        strings = strings,
                        context = context
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

@Composable
fun OrgItem(
    org: UserBrief,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(avatarUrl = org.avatarUrl, size = 48)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = org.login,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (org.type == "Organization") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.getOrganizationType(context),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}