package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.domain.model.IssueLabel
import com.kingzcheung.kithub.domain.model.IssueState
import com.kingzcheung.kithub.domain.model.UserBrief
import com.kingzcheung.kithub.presentation.viewmodel.IssueDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetailScreen(
    onNavigateToUser: (String) -> Unit = {},
    viewModel: IssueDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.issue != null) {
                        Text(
                            text = "#${state.issue!!.number}",
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
                    if (state.issue != null) {
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
                    Button(onClick = { viewModel.loadIssue() }) {
                        Text(strings.getRetry(context))
                    }
                }
            }
        } else if (state.issue != null) {
            IssueDetailContent(
                issue = state.issue!!,
                paddingValues = paddingValues,
                onNavigateToUser = onNavigateToUser,
                strings = strings,
                context = context
            )
        }
    }
}

@Composable
fun IssueDetailContent(
    issue: Issue,
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
            IssueHeader(issue = issue, strings = strings, context = context)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            IssueMetaInfo(issue = issue, strings = strings, context = context)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (issue.labels.isNotEmpty()) {
            item {
                Text(
                    text = strings.getLabels(context),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LabelsRow(labels = issue.labels)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        if (issue.body != null && issue.body.isNotEmpty()) {
            item {
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
                        text = issue.body,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        if (issue.assignees.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.getAssignees(context),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                AssigneesList(
                    assignees = issue.assignees,
                    onNavigateToUser = onNavigateToUser
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = strings.getCommentsFormat(context, issue.comments),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun IssueHeader(
    issue: Issue,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IssueStateBadge(state = issue.state, strings = strings, context = context)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = issue.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun IssueStateBadge(
    state: IssueState,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    val (color, icon, text) = when (state) {
        IssueState.OPEN -> Triple(
            Color(0xFF238636),
            Icons.Default.CheckCircle,
            strings.getOpen(context)
        )
        IssueState.CLOSED -> Triple(
            Color(0xFFDA3633),
            Icons.Default.Cancel,
            strings.getClosed(context)
        )
        IssueState.REOPENED -> Triple(
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
fun IssueMetaInfo(
    issue: Issue,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = issue.user.avatarUrl,
            contentDescription = issue.user.login,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = issue.user.login,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )
                Text(
                    text = strings.getOpenedThisIssue(context),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = issue.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LabelsRow(labels: List<IssueLabel>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            LabelChip(label = label)
        }
    }
}

@Composable
fun LabelChip(label: IssueLabel) {
    val color = try {
        Color(android.graphics.Color.parseColor("#${label.color}"))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = label.name,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AssigneesList(
    assignees: List<UserBrief>,
    onNavigateToUser: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        assignees.forEach { assignee ->
            AssigneeItem(assignee = assignee, onClick = { onNavigateToUser(assignee.login) })
        }
    }
}

@Composable
fun AssigneeItem(
    assignee: UserBrief,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = assignee.avatarUrl,
                contentDescription = assignee.login,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = assignee.login,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}