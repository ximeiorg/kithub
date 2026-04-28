package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.Event
import com.kingzcheung.kithub.domain.model.EventType
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.domain.model.UserBrief
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun ProfileScreen(
    onNavigateToUser: (String) -> Unit,
    onNavigateToRepository: (String, String) -> Unit,
    onNavigateToRepos: () -> Unit,
    onNavigateToOrgs: () -> Unit,
    onNavigateToStarred: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(strings.getProfile(context)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = strings.getSettings(context))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && state.user == null) {
            ErrorState(
                message = state.error ?: strings.getUnknown(context),
                onRetry = { viewModel.loadProfile() },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            state.user?.let { user ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ProfileUserInfo(user = user, strings = strings, context = context)
                    }
                    
                    if (state.pinnedRepos.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = strings.getPinnedRepositories(context),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PinnedReposRow(
                                    repos = state.pinnedRepos,
                                    onRepoClick = { repo ->
                                        onNavigateToRepository(repo.owner.login, repo.name)
                                    }
                                )
                            }
                        }
                    }
                    
                    item {
                        ProfileMenuCard(
                            reposCount = user.publicRepos,
                            orgsCount = state.orgs.size,
                            onReposClick = onNavigateToRepos,
                            onOrgsClick = onNavigateToOrgs,
                            onStarredClick = onNavigateToStarred,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            strings = strings,
                            context = context
                        )
                    }
                    
                    if (state.events.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = strings.getContributionActivity(context),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        items(state.events, key = { it.id }) { event ->
                            EventItem(
                                event = event,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                strings = strings,
                                context = context
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinnedReposRow(
    repos: List<Repository>,
    onRepoClick: (Repository) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(repos, key = { it.id }) { repo ->
            PinnedRepoCard(
                repo = repo,
                onClick = { onRepoClick(repo) }
            )
        }
    }
}

@Composable
fun PinnedRepoCard(
    repo: Repository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = repo.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (repo.language != null) {
                    LanguageBadge(language = repo.language)
                }
                IconText(
                    icon = Icons.Rounded.Star,
                    text = repo.stargazersCount.toString()
                )
            }
        }
    }
}

@Composable
fun ProfileUserInfo(
    user: com.kingzcheung.kithub.domain.model.User,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(avatarUrl = user.avatarUrl, size = 80)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name ?: user.login,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = user.login,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (user.bio != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            if (user.email != null && user.email.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatItem(label = strings.getFollowers(context), count = user.followers)
                StatItem(label = strings.getFollowing(context), count = user.following)
                StatItem(label = strings.getReposTab(context), count = user.publicRepos)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StatItem(
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuCard(
    reposCount: Int,
    orgsCount: Int,
    onReposClick: () -> Unit,
    onOrgsClick: () -> Unit,
    onStarredClick: () -> Unit,
    modifier: Modifier = Modifier,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileMenuRow(
                icon = Icons.Default.Book,
                title = strings.getRepositories(context),
                count = reposCount,
                onClick = onReposClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ProfileMenuRow(
                icon = Icons.Default.Group,
                title = strings.getOrganizations(context),
                count = orgsCount,
                onClick = onOrgsClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ProfileMenuRow(
                icon = Icons.Rounded.Star,
                title = strings.getStarred(context),
                count = null,
                onClick = onStarredClick
            )
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    count: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        if (count != null) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EventItem(
    event: Event,
    modifier: Modifier = Modifier,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getEventIcon(event.type),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getEventTitle(event, strings, context),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (event.repo != null) {
                    Text(
                        text = event.repo.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun getEventIcon(type: EventType): ImageVector {
    return when (type) {
        EventType.PushEvent -> Icons.Default.Upload
        EventType.CreateEvent -> Icons.Default.Add
        EventType.DeleteEvent -> Icons.Default.Delete
        EventType.ForkEvent -> Icons.Default.ForkRight
        EventType.WatchEvent -> Icons.Rounded.Star
        EventType.IssuesEvent -> Icons.Default.Report
        EventType.IssueCommentEvent -> Icons.AutoMirrored.Filled.Comment
        EventType.PullRequestEvent -> Icons.Default.Merge
        EventType.PullRequestReviewEvent -> Icons.Default.Check
        EventType.PullRequestReviewCommentEvent -> Icons.AutoMirrored.Filled.Comment
        EventType.ReleaseEvent -> Icons.Default.NewReleases
        EventType.PublicEvent -> Icons.Default.Public
        else -> Icons.Default.Code
    }
}

fun getEventTitle(event: Event, strings: com.kingzcheung.kithub.util.Strings, context: android.content.Context): String {
    val repoName = event.repo?.name ?: strings.getUnknown(context)
    return when (event.type) {
        EventType.PushEvent -> strings.getPushedTo(context) + " $repoName"
        EventType.CreateEvent -> strings.getCreated(context) + " ${event.payload?.get("ref_type") ?: "repository"} in $repoName"
        EventType.DeleteEvent -> strings.getDeleted(context) + " ${event.payload?.get("ref_type") ?: "branch"} in $repoName"
        EventType.ForkEvent -> strings.getForked(context) + " $repoName"
        EventType.WatchEvent -> strings.getStarredRepo(context) + " $repoName"
        EventType.IssuesEvent -> "${event.payload?.get("action") ?: "Updated"} ${strings.getIssues(context).lowercase()} in $repoName"
        EventType.IssueCommentEvent -> strings.getCommentedOn(context) + " ${strings.getIssues(context).lowercase()} in $repoName"
        EventType.PullRequestEvent -> "${event.payload?.get("action") ?: "Updated"} PR in $repoName"
        EventType.PullRequestReviewEvent -> strings.getReviewedPrIn(context) + " $repoName"
        EventType.PullRequestReviewCommentEvent -> strings.getCommentedOnPrIn(context) + " $repoName"
        EventType.ReleaseEvent -> strings.getPublishedRelease(context, repoName)
        EventType.PublicEvent -> strings.getMadePublic(context, repoName)
        else -> strings.getActivityIn(context, repoName)
    }
}