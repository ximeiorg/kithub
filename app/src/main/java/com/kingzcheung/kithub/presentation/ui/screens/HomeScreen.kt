package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kingzcheung.kithub.domain.model.Event
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.presentation.ui.components.RepositoryCard
import com.kingzcheung.kithub.presentation.ui.components.formatRelativeTime
import com.kingzcheung.kithub.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRepository: (String, String) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateToIssues: () -> Unit,
    onNavigateToPullRequests: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            viewModel.refresh()
        }
    }
    
    LaunchedEffect(state.loading) {
        if (!state.loading) {
            pullToRefreshState.endRefresh()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.user?.let { user ->
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.login,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = user.login,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        QuickAccessSection(
                            onNavigateToIssues = onNavigateToIssues,
                            onNavigateToPullRequests = onNavigateToPullRequests
                        )
                    }
                    
                    if (state.repos.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Your Repositories",
                                icon = Icons.Outlined.Folder,
                                count = state.repos.size
                            )
                        }
                        items(
                            items = state.repos.take(5),
                            key = { "repo_${it.id}_${it.name}" }
                        ) { repo ->
                            RepositoryCard(
                                repo = repo,
                                onClick = { onNavigateToRepository(repo.owner.login, repo.name) }
                            )
                        }
                    }
                    
                    if (state.starred.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Starred",
                                icon = Icons.Outlined.Star,
                                count = state.starred.size
                            )
                        }
                        items(
                            items = state.starred.take(5),
                            key = { "starred_${it.id}_${it.name}" }
                        ) { repo ->
                            RepositoryCard(
                                repo = repo,
                                onClick = { onNavigateToRepository(repo.owner.login, repo.name) }
                            )
                        }
                    }
                    
                    if (state.events.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Recent Activity",
                                icon = Icons.Outlined.Notifications,
                                count = state.events.size
                            )
                        }
                        items(
                            items = state.events,
                            key = { "event_${it.id}_${it.createdAt}" }
                        ) { event ->
                            EventCard(
                                event = event,
                                onRepoClick = {
                                    val repoName = event.repo?.name
                                    if (repoName != null) {
                                        val parts = repoName.split("/")
                                        if (parts.size == 2) {
                                            onNavigateToRepository(parts[0], parts[1])
                                        }
                                    }
                                },
                                onUserClick = {
                                    event.actor?.login?.let { login ->
                                        onNavigateToUser(login)
                                    }
                                }
                            )
                        }
                    }
                    
                    if (state.orgs.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Organizations",
                                icon = Icons.Outlined.Groups,
                                count = state.orgs.size
                            )
                        }
                        items(
                            items = state.orgs,
                            key = { "org_${it.id}_${it.login}" }
                        ) { org ->
                            OrganizationCard(
                                org = org,
                                onClick = { onNavigateToUser(org.login) }
                            )
                        }
                    }
                }
                
                PullToRefreshContainer(
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun QuickAccessSection(
    onNavigateToIssues: () -> Unit,
    onNavigateToPullRequests: () -> Unit
) {
    Column {
        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Issues",
                icon = Icons.Outlined.BugReport,
                color = MaterialTheme.colorScheme.error,
                onClick = onNavigateToIssues
            )
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Pull Requests",
                icon = Icons.Outlined.CallSplit,
                color = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToPullRequests
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = color
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EventCard(
    event: Event,
    onRepoClick: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onRepoClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                event.actor?.let { actor ->
                    AsyncImage(
                        model = actor.avatarUrl,
                        contentDescription = actor.login,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = actor.login,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = getEventActionText(event.type),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            event.repo?.let { repo ->
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = formatRelativeTime(event.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrganizationCard(
    org: com.kingzcheung.kithub.domain.model.UserBrief,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = org.avatarUrl,
                contentDescription = org.login,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = org.login,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = org.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun getEventActionText(type: com.kingzcheung.kithub.domain.model.EventType): String {
    return when (type) {
        com.kingzcheung.kithub.domain.model.EventType.PushEvent -> "pushed to"
        com.kingzcheung.kithub.domain.model.EventType.PullRequestEvent -> "opened PR in"
        com.kingzcheung.kithub.domain.model.EventType.IssuesEvent -> "opened issue in"
        com.kingzcheung.kithub.domain.model.EventType.WatchEvent -> "starred"
        com.kingzcheung.kithub.domain.model.EventType.ForkEvent -> "forked"
        com.kingzcheung.kithub.domain.model.EventType.CreateEvent -> "created"
        com.kingzcheung.kithub.domain.model.EventType.DeleteEvent -> "deleted"
        com.kingzcheung.kithub.domain.model.EventType.ReleaseEvent -> "released"
        com.kingzcheung.kithub.domain.model.EventType.IssueCommentEvent -> "commented on"
        com.kingzcheung.kithub.domain.model.EventType.CommitCommentEvent -> "commented on commit in"
        com.kingzcheung.kithub.domain.model.EventType.PullRequestReviewEvent -> "reviewed PR in"
        com.kingzcheung.kithub.domain.model.EventType.PullRequestReviewCommentEvent -> "commented on PR in"
        else -> "acted on"
    }
}