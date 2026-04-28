package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.PullRequest
import com.kingzcheung.kithub.domain.model.PullRequestBranch
import com.kingzcheung.kithub.presentation.viewmodel.PullRequestDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRequestDetailScreen(
    onNavigateToUser: (String) -> Unit = {},
    viewModel: PullRequestDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.pullRequest != null) {
                        Text(
                            text = "PR #${state.pullRequest!!.number}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = strings.getBack(context))
                    }
                },
                actions = {
                    if (state.pullRequest != null) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Share, contentDescription = strings.getShare(context))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = strings.getUnknown(context),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadPullRequest() }) {
                        Text(strings.getRetry(context))
                    }
                }
            }
        } else if (state.pullRequest != null) {
            PullRequestDetailContent(
                pullRequest = state.pullRequest!!,
                paddingValues = paddingValues,
                onNavigateToUser = onNavigateToUser,
                strings = strings,
                context = context
            )
        }
    }
}

@Composable
fun PullRequestDetailContent(
    pullRequest: PullRequest,
    paddingValues: PaddingValues,
    onNavigateToUser: (String) -> Unit = {},
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            PullRequestHeader(pullRequest = pullRequest, strings = strings, context = context)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            PullRequestBranchInfo(pullRequest = pullRequest, strings = strings, context = context)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            PullRequestMetaInfo(pullRequest = pullRequest, onNavigateToUser = onNavigateToUser, strings = strings, context = context)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            PullRequestStats(pullRequest = pullRequest, strings = strings, context = context)
        }
        
        if (pullRequest.labels.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.getLabels(context),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LabelsRow(labels = pullRequest.labels)
            }
        }
        
        if (pullRequest.body != null && pullRequest.body.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.getDescription(context),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Text(
                        text = pullRequest.body,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        if (pullRequest.assignees.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.getAssignees(context),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                AssigneesList(
                    assignees = pullRequest.assignees,
                    onNavigateToUser = onNavigateToUser
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = strings.getCommitsCount(context, pullRequest.commits),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strings.getCommentsFormat(context, pullRequest.comments),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strings.getReviewsFormat(context, pullRequest.reviewComments),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PullRequestHeader(
    pullRequest: PullRequest,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        PullRequestStateBadge(
            state = pullRequest.state,
            merged = pullRequest.merged,
            draft = pullRequest.draft,
            strings = strings,
            context = context
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = pullRequest.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PullRequestStateBadge(
    state: com.kingzcheung.kithub.domain.model.IssueState,
    merged: Boolean,
    draft: Boolean,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    val (color, icon, text) = if (merged) {
        Triple(
            Color(0xFF6F42C1),
            Icons.Default.Merge,
            strings.getMerged(context)
        )
    } else if (draft) {
        Triple(
            Color(0xFF808080),
            Icons.Default.Edit,
            strings.getDraftLabel(context)
        )
    } else when (state) {
        com.kingzcheung.kithub.domain.model.IssueState.OPEN -> Triple(
            Color(0xFF238636),
            Icons.Default.CheckCircle,
            strings.getOpen(context)
        )
        com.kingzcheung.kithub.domain.model.IssueState.CLOSED -> Triple(
            Color(0xFFDA3633),
            Icons.Default.Cancel,
            strings.getClosed(context)
        )
        com.kingzcheung.kithub.domain.model.IssueState.REOPENED -> Triple(
            Color(0xFF238636),
            Icons.Default.Refresh,
            strings.getOpen(context)
        )
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
fun PullRequestBranchInfo(
    pullRequest: PullRequest,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BranchInfo(branch = pullRequest.head, label = strings.getFromBranch(context))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = strings.getIntoBranch(context),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BranchInfo(branch = pullRequest.base, label = strings.getIntoBranch(context))
        }
    }
}

@Composable
fun BranchInfo(branch: PullRequestBranch, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = branch.ref,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        if (branch.user != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "@${branch.user.login}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun PullRequestMetaInfo(
    pullRequest: PullRequest,
    onNavigateToUser: (String) -> Unit,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = pullRequest.user.avatarUrl,
            contentDescription = pullRequest.user.login,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pullRequest.user.login,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " ${strings.getWantsToMerge(context)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = pullRequest.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PullRequestStats(
    pullRequest: PullRequest,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = strings.getAdditions(context),
            value = "+${pullRequest.additions}",
            color = Color(0xFF238636)
        )
        StatCard(
            label = strings.getDeletions(context),
            value = "-${pullRequest.deletions}",
            color = Color(0xFFDA3633)
        )
        StatCard(
            label = strings.getFilesChanged(context),
            value = "${pullRequest.changedFiles}",
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}