package com.kingzcheung.kithub.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.domain.model.IssueState
import com.kingzcheung.kithub.domain.model.PullRequest

@Composable
fun IssueCard(
    issue: Issue,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IssueStateIcon(state = issue.state)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "#${issue.number}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (issue.labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    issue.labels.take(3).forEach { label ->
                        LabelChip(label = label.name, color = parseColor(label.color))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(avatarUrl = issue.user.avatarUrl, size = 20)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = issue.user.login,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "opened ${formatRelativeTime(issue.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PullRequestCard(
    pr: PullRequest,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PullRequestStateIcon(
                    state = pr.state,
                    merged = pr.merged,
                    draft = pr.draft
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "#${pr.number}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = pr.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (pr.draft) {
                    Spacer(modifier = Modifier.width(8.dp))
                    DraftBadge()
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(avatarUrl = pr.user.avatarUrl, size = 20)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pr.user.login,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${pr.base.ref} ← ${pr.head.ref}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun IssueStateIcon(state: IssueState) {
    Icon(
        imageVector = when (state) {
            IssueState.OPEN -> Icons.Default.ErrorOutline
            IssueState.CLOSED -> Icons.Default.CheckCircle
            IssueState.REOPENED -> Icons.Default.ErrorOutline
        },
        contentDescription = state.toApiValue(),
        tint = when (state) {
            IssueState.OPEN -> MaterialTheme.colorScheme.primary
            IssueState.CLOSED -> MaterialTheme.colorScheme.error
            IssueState.REOPENED -> MaterialTheme.colorScheme.primary
        }
    )
}

@Composable
fun PullRequestStateIcon(
    state: IssueState,
    merged: Boolean,
    draft: Boolean
) {
    val icon = when {
        merged -> Icons.Default.CheckCircle
        draft -> Icons.Default.Edit
        state == IssueState.OPEN -> Icons.Default.ErrorOutline
        state == IssueState.CLOSED -> Icons.Default.Cancel
        else -> Icons.Default.ErrorOutline
    }
    
    val color = when {
        merged -> Color(0xFF6F42C1)
        draft -> MaterialTheme.colorScheme.onSurfaceVariant
        state == IssueState.OPEN -> MaterialTheme.colorScheme.primary
        state == IssueState.CLOSED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }
    
    Icon(
        imageVector = icon,
        contentDescription = "PR state",
        tint = color
    )
}

@Composable
fun DraftBadge() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = "Draft",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun LabelChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.3f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = getContrastColor(color),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor("#$colorString"))
    } catch (e: Exception) {
        Color.Gray
    }
}

fun getContrastColor(color: Color): Color {
    val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
    return if (luminance > 0.5f) Color.Black else Color.White
}

fun formatRelativeTime(dateString: String): String {
    return try {
        val date = java.time.Instant.parse(dateString)
        val now = java.time.Instant.now()
        val diff = java.time.Duration.between(date, now)
        
        when {
            diff.toDays() > 365 -> "${diff.toDays() / 365} years ago"
            diff.toDays() > 30 -> "${diff.toDays() / 30} months ago"
            diff.toDays() > 0 -> "${diff.toDays()} days ago"
            diff.toHours() > 0 -> "${diff.toHours()} hours ago"
            diff.toMinutes() > 0 -> "${diff.toMinutes()} minutes ago"
            else -> "just now"
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun IssueSearchCard(
    issue: Issue,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (issue.repositoryOwner.isNotEmpty() && issue.repositoryName.isNotEmpty()) {
                Text(
                    text = "${issue.repositoryOwner}/${issue.repositoryName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IssueStateIcon(state = issue.state)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "#${issue.number}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (issue.labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    issue.labels.take(3).forEach { label ->
                        LabelChip(label = label.name, color = parseColor(label.color))
                    }
                    if (issue.labels.size > 3) {
                        Text(
                            text = "+${issue.labels.size - 3}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserAvatar(avatarUrl = issue.user.avatarUrl, size = 20)
                Text(
                    text = issue.user.login,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "opened ${formatRelativeTime(issue.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (issue.comments > 0) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = issue.comments.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}