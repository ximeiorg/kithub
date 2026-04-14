package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.NotificationReason
import com.kingzcheung.kithub.domain.model.NotificationThread
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showMarkAllDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    IconButton(onClick = { viewModel.toggleShowAll() }) {
                        Icon(
                            imageVector = if (state.showAll) Icons.Default.FilterList else Icons.Default.FilterAlt,
                            contentDescription = if (state.showAll) "Show unread only" else "Show all"
                        )
                    }
                    IconButton(onClick = { showMarkAllDialog = true }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Mark all read")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && state.notifications.isEmpty()) {
            ErrorState(
                message = state.error ?: "Unknown error",
                onRetry = { viewModel.loadNotifications() },
                modifier = Modifier.padding(paddingValues)
            )
        } else if (state.notifications.isEmpty()) {
            EmptyState(
                message = if (state.showAll) "No notifications found" else "No unread notifications",
                modifier = Modifier.padding(paddingValues),
                action = {
                    if (!state.showAll) {
                        TextButton(onClick = { viewModel.toggleShowAll() }) {
                            Text("Show all notifications")
                        }
                    }
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = { viewModel.markAsRead(notification.id) },
                        onMarkAsDone = { viewModel.markAsDone(notification.id) }
                    )
                }
                
                if (state.hasMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.loading) {
                                CircularProgressIndicator()
                            } else {
                                TextButton(onClick = { viewModel.loadMoreNotifications() }) {
                                    Text("Load more")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showMarkAllDialog) {
        AlertDialog(
            onDismissRequest = { showMarkAllDialog = false },
            title = { Text("Mark all as read") },
            text = { Text("Are you sure you want to mark all notifications as read?") },
            confirmButton = {
                Button(onClick = {
                    showMarkAllDialog = false
                    viewModel.markAllAsRead()
                }) {
                    Text("Mark all read")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMarkAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NotificationCard(
    notification: NotificationThread,
    onMarkAsRead: () -> Unit,
    onMarkAsDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.unread) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                if (notification.unread) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Icon(
                    imageVector = getNotificationTypeIcon(notification.subject.type),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = notification.repository.fullName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = notification.subject.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotificationReasonBadge(reason = notification.reason)
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(
                    onClick = onMarkAsRead,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark as read",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = onMarkAsDone,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Mark as done",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationReasonBadge(
    reason: NotificationReason,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = getReasonColor(reason)
    ) {
        Text(
            text = reason.displayText(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun getNotificationTypeIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "issue" -> Icons.Default.Report
        "pullrequest" -> Icons.Default.Merge
        "commit" -> Icons.Default.Commit
        "release" -> Icons.Default.NewReleases
        "discussion" -> Icons.Default.Forum
        else -> Icons.Default.Notifications
    }
}

fun getReasonColor(reason: NotificationReason): androidx.compose.ui.graphics.Color {
    return when (reason) {
        NotificationReason.Mention,
        NotificationReason.TeamMention -> androidx.compose.ui.graphics.Color(0xFFE91E63)
        NotificationReason.Assign,
        NotificationReason.ReviewRequested -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        NotificationReason.Author -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
        NotificationReason.Comment -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        NotificationReason.SecurityAlert -> androidx.compose.ui.graphics.Color(0xFFFF5722)
        else -> androidx.compose.ui.graphics.Color.Gray
    }
}