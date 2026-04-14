package com.kingzcheung.kithub.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kingzcheung.kithub.domain.model.Commit
import com.kingzcheung.kithub.domain.model.CommitBrief
import com.kingzcheung.kithub.domain.model.Content
import com.kingzcheung.kithub.domain.model.ContentType

@Composable
fun CommitCard(
    commit: CommitBrief,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = commit.sha.take(7),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = commit.message ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (commit.author != null) {
                    UserAvatar(avatarUrl = commit.author.avatarUrl, size = 20)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = commit.author.login,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    content: Content,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = when (content.type) {
                    ContentType.DIR -> Icons.Default.Folder
                    ContentType.FILE -> Icons.Default.Description
                    ContentType.SYMLINK -> Icons.Default.Link
                    ContentType.SUBMODULE -> Icons.Default.Source
                },
                contentDescription = content.type.toApiValue(),
                tint = when (content.type) {
                    ContentType.DIR -> MaterialTheme.colorScheme.primary
                    ContentType.FILE -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        headlineContent = {
            Text(
                text = content.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = if (content.type == ContentType.FILE && content.size > 0) {
            { Text(formatFileSize(content.size)) }
        } else null
    )
}

fun formatFileSize(size: Int): String {
    val kb = size / 1024.0
    val mb = kb / 1024.0
    
    return when {
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$size B"
    }
}