package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.Event
import com.kingzcheung.kithub.domain.model.EventType
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.presentation.ui.components.ErrorState
import com.kingzcheung.kithub.presentation.ui.components.LanguageBadge
import com.kingzcheung.kithub.presentation.ui.components.UserAvatar
import com.kingzcheung.kithub.presentation.ui.components.formatRelativeTime
import com.kingzcheung.kithub.presentation.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToUser: (String) -> Unit,
    onNavigateToRepository: (String, String) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    val listState = rememberLazyListState()
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
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= state.events.size - 5 && !state.loadingMore) {
                    viewModel.loadMoreEvents()
                }
            }
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(strings.getExplore(context)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
        } else if (state.error != null && state.events.isEmpty()) {
            ErrorState(
                message = state.error ?: strings.getUnknown(context),
                onRetry = { viewModel.loadEvents() },
                modifier = Modifier.padding(paddingValues)
            )
        } else if (state.events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = strings.getNoActivity(context),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = strings.getFollowSomeUsers(context),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
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
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.events, key = { it.id }) { event ->
                        val repoDetail = event.repo?.name?.let { state.repoDetails[it] }
                        ExploreEventItem(
                            event = event,
                            repoDetail = repoDetail,
                            onUserClick = { event.actor?.login?.let { onNavigateToUser(it) } },
                            onRepoClick = {
                                event.repo?.name?.let { name ->
                                    val parts = name.split("/")
                                    if (parts.size == 2) {
                                        onNavigateToRepository(parts[0], parts[1])
                                    }
                                }
                            },
                            strings = strings,
                            context = context
                        )
                    }
                    
                    if (state.loadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
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
fun ExploreEventItem(
    event: Event,
    repoDetail: Repository? = null,
    onUserClick: () -> Unit,
    onRepoClick: () -> Unit,
    modifier: Modifier = Modifier,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    avatarUrl = event.actor?.avatarUrl ?: "",
                    size = 40,
                    modifier = Modifier.clickable(onClick = onUserClick)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = event.actor?.login ?: strings.getUnknown(context),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable(onClick = onUserClick)
                        )
                        EventTypeBadge(type = event.type)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = getEventDescription(event, strings, context),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatRelativeTime(event.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            if (event.repo != null) {
                Spacer(modifier = Modifier.height(12.dp))
                EventRepoCard(
                    event = event,
                    repoDetail = repoDetail,
                    onRepoClick = onRepoClick,
                    strings = strings,
                    context = context
                )
            }
        }
    }
}

@Composable
fun EventRepoCard(
    event: Event,
    repoDetail: Repository?,
    onRepoClick: () -> Unit,
    modifier: Modifier = Modifier,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onRepoClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (repoDetail != null) {
                    UserAvatar(
                        avatarUrl = repoDetail.owner.avatarUrl,
                        size = 20
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.repo!!.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (repoDetail != null && event.type != EventType.PullRequestEvent && event.type != EventType.IssuesEvent && event.type != EventType.ReleaseEvent && event.type != EventType.PushEvent && event.type != EventType.ForkEvent) {
                Spacer(modifier = Modifier.height(8.dp))
                if (repoDetail.description != null) {
                    Text(
                        text = repoDetail.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (repoDetail.language != null) {
                        LanguageBadge(language = repoDetail.language)
                    }
                    IconText(
                        icon = Icons.Rounded.Star,
                        text = repoDetail.stargazersCount.toString()
                    )
                    IconText(
                        icon = Icons.AutoMirrored.Filled.CallSplit,
                        text = repoDetail.forksCount.toString()
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                EventPayloadDetails(event = event, strings = strings, context = context)
            }
        }
    }
}

@Composable
fun EventPayloadDetails(
    event: Event,
    modifier: Modifier = Modifier,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    when (event.type) {
        EventType.PushEvent -> {
            val ref = event.payload?.get("ref") as? String
            val size = event.payload?.get("size") as? Int
            if (ref != null) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.getBranchFormat(context, ref.removePrefix("refs/heads/")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (size != null && size > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.getCommitsInBranch(context, size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        EventType.CreateEvent -> {
            val ref = event.payload?.get("ref") as? String
            val refType = event.payload?.get("ref_type") as? String
            if (ref != null && refType != null) {
                Text(
                    text = "$refType: $ref",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        EventType.DeleteEvent -> {
            val ref = event.payload?.get("ref") as? String
            val refType = event.payload?.get("ref_type") as? String
            if (ref != null && refType != null) {
                Text(
                    text = strings.getDeletedRef(context, "$refType: $ref"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        EventType.PullRequestEvent -> {
            val pr = event.payload?.get("pull_request") as? Map<String, Any?>
            val number = pr?.get("number") as? Int
            val title = pr?.get("title") as? String
            val body = pr?.get("body") as? String
            val state = pr?.get("state") as? String
            val merged = pr?.get("merged") as? Boolean
            val base = pr?.get("base") as? Map<String, Any?>
            val head = pr?.get("head") as? Map<String, Any?>
            val baseRef = base?.get("ref") as? String
            val headRef = head?.get("ref") as? String
            val additions = pr?.get("additions") as? Int
            val deletions = pr?.get("deletions") as? Int
            
            Column(modifier = modifier) {
                if (number != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val stateColor = when {
                            merged == true -> MaterialTheme.colorScheme.tertiary
                            state == "open" -> MaterialTheme.colorScheme.primary
                            state == "closed" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val stateIcon = when {
                            merged == true -> Icons.Default.Check
                            state == "open" -> Icons.Default.Merge
                            state == "closed" -> Icons.Default.Close
                            else -> Icons.Default.Code
                        }
                        Icon(
                            imageVector = stateIcon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = stateColor
                        )
                        Text(
                            text = "#$number",
                            style = MaterialTheme.typography.labelSmall,
                            color = stateColor
                        )
                        if (title != null) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (baseRef != null && headRef != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = headRef,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = baseRef,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (additions != null || deletions != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (additions != null) {
                                Text(
                                    text = "+$additions",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            if (deletions != null) {
                                Text(
                                    text = "-$deletions",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    if (!body.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = body.take(100).let { if (body.length > 100) "$it..." else it },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = strings.getPullRequest(context),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        EventType.ReleaseEvent -> {
            val release = event.payload?.get("release") as? Map<String, Any?>
            val tagName = release?.get("tag_name") as? String
            val releaseName = release?.get("name") as? String
            if (tagName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tagName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    if (releaseName != null && releaseName != tagName) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = releaseName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        EventType.IssuesEvent -> {
            val issue = event.payload?.get("issue") as? Map<String, Any?>
            val number = issue?.get("number") as? Int
            val title = issue?.get("title") as? String
            if (number != null && title != null) {
                Text(
                    text = "#$number $title",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        EventType.ForkEvent -> {
            val forkee = event.payload?.get("forkee") as? Map<String, Any?>
            val fullName = forkee?.get("full_name") as? String
            if (fullName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ForkRight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = strings.getForkedTo(context, fullName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        else -> {}
    }
}

@Composable
fun IconText(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EventTypeBadge(
    type: EventType,
    modifier: Modifier = Modifier
) {
    val (text, color) = remember(type) {
        when (type) {
            EventType.PushEvent -> "Push" to true
            EventType.CreateEvent -> "Create" to true
            EventType.DeleteEvent -> "Delete" to false
            EventType.ForkEvent -> "Fork" to true
            EventType.WatchEvent -> "Star" to true
            EventType.IssuesEvent -> "Issue" to false
            EventType.IssueCommentEvent -> "Comment" to true
            EventType.PullRequestEvent -> "PR" to true
            EventType.PullRequestReviewEvent -> "Review" to true
            EventType.PullRequestReviewCommentEvent -> "Comment" to true
            EventType.ReleaseEvent -> "Release" to true
            EventType.PublicEvent -> "Public" to true
            else -> "Activity" to true
        }
    }
    
    val badgeColor = if (color) MaterialTheme.colorScheme.primary
                     else MaterialTheme.colorScheme.error
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = badgeColor.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun getEventDescription(event: Event, strings: com.kingzcheung.kithub.util.Strings, context: android.content.Context): String {
    val action = event.payload?.get("action") as? String
    return when (event.type) {
        EventType.PushEvent -> {
            val size = event.payload?.get("size") as? Int ?: 0
            strings.getPushedCommits(context, size)
        }
        EventType.CreateEvent -> strings.getCreatedRef(context, "${event.payload?.get("ref_type") ?: "repository"}")
        EventType.DeleteEvent -> strings.getDeletedRef(context, "${event.payload?.get("ref_type") ?: "branch"}")
        EventType.ForkEvent -> strings.getForked(context) + " ${strings.getRepositories(context).lowercase()}"
        EventType.WatchEvent -> strings.getStarredRepo(context) + " ${strings.getRepositories(context).lowercase()}"
        EventType.IssuesEvent -> "${action ?: strings.getUnknown(context)} ${strings.getIssues(context).lowercase()}"
        EventType.IssueCommentEvent -> strings.getCommentedOn(context) + " ${strings.getIssues(context).lowercase()}"
        EventType.PullRequestEvent -> {
            val pr = event.payload?.get("pull_request") as? Map<String, Any?>
            val number = pr?.get("number") as? Int
            val prAction = event.payload?.get("action") as? String
            val merged = pr?.get("merged") as? Boolean
            when {
                merged == true -> strings.getMergedPr(context, number ?: 0)
                prAction == "closed" -> strings.getClosedPr(context, number ?: 0)
                prAction == "opened" -> strings.getOpenedPr(context, number ?: 0)
                prAction == "reopened" -> strings.getReopenedPr(context, number ?: 0)
                else -> strings.getUpdatedPr(context, number ?: 0)
            }
        }
        EventType.PullRequestReviewEvent -> {
            val reviewState = event.payload?.get("review")?.let { it as? Map<String, Any?> }?.get("state") as? String
            strings.getReviewedPr(context, reviewState ?: strings.getUnknown(context))
        }
        EventType.PullRequestReviewCommentEvent -> strings.getCommentedOnPrReview(context)
        EventType.ReleaseEvent -> {
            val tagName = event.payload?.get("release")?.let { it as? Map<String, Any?> }?.get("tag_name") as? String
            strings.getReleased(context) + " $tagName"
        }
        EventType.PublicEvent -> strings.getMadePublic(context, "${strings.getRepositories(context).lowercase()}")
        else -> strings.getUnknown(context)
    }
}