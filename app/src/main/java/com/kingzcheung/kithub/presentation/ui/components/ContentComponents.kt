package com.kingzcheung.kithub.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                .padding(12.dp)
        ) {
            Text(
                text = commit.sha.take(7),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = commit.message ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (commit.author != null) {
                    UserAvatar(avatarUrl = commit.author.avatarUrl, size = 18)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = commit.author.login,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getFileIcon(content.name, content.type),
            contentDescription = content.type.toApiValue(),
            modifier = Modifier.size(24.dp),
            tint = getFileIconColor(content.name, content.type)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = content.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (content.type == ContentType.FILE && content.size > 0) {
            Text(
                text = formatFileSize(content.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun getFileIcon(fileName: String, type: ContentType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        ContentType.DIR -> Icons.Outlined.Folder
        ContentType.FILE -> getFileTypeIcon(fileName)
        ContentType.SYMLINK -> Icons.Outlined.Link
        ContentType.SUBMODULE -> Icons.Outlined.Source
    }
}

@Composable
fun getFileTypeIcon(fileName: String): androidx.compose.ui.graphics.vector.ImageVector {
    val name = fileName.lowercase()
    return when {
        name.endsWith(".kt") || name.endsWith(".kts") -> Icons.Outlined.Code
        name.endsWith(".java") -> Icons.Outlined.Code
        name.endsWith(".py") -> Icons.Outlined.Code
        name.endsWith(".js") || name.endsWith(".ts") -> Icons.Outlined.Code
        name.endsWith(".json") -> Icons.Outlined.DataObject
        name.endsWith(".xml") -> Icons.Outlined.Code
        name.endsWith(".md") || name.endsWith(".markdown") -> Icons.Outlined.Description
        name.endsWith(".txt") -> Icons.Outlined.Description
        name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || 
            name.endsWith(".gif") || name.endsWith(".webp") -> Icons.Outlined.Image
        name.endsWith(".gradle") || name.endsWith(".gradle.kts") -> Icons.Outlined.Build
        name.endsWith(".properties") || name.endsWith(".yaml") || name.endsWith(".yml") -> Icons.Outlined.Settings
        name.endsWith(".gitignore") -> Icons.Outlined.Source
        else -> Icons.Outlined.InsertDriveFile
    }
}

@Composable
fun getFileIconColor(fileName: String, type: ContentType): Color {
    if (type != ContentType.FILE) {
        return when (type) {
            ContentType.DIR -> MaterialTheme.colorScheme.primary
            ContentType.SYMLINK -> MaterialTheme.colorScheme.tertiary
            ContentType.SUBMODULE -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    
    val name = fileName.lowercase()
    return when {
        name.endsWith(".kt") || name.endsWith(".kts") -> Color(0xFF7F52FF)
        name.endsWith(".java") -> Color(0xFFB07219)
        name.endsWith(".py") -> Color(0xFF3572A5)
        name.endsWith(".js") -> Color(0xFFF7DF1E)
        name.endsWith(".ts") -> Color(0xFF3178C6)
        name.endsWith(".json") -> MaterialTheme.colorScheme.tertiary
        name.endsWith(".md") || name.endsWith(".markdown") -> MaterialTheme.colorScheme.onSurfaceVariant
        name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif") -> Color(0xFF8B5CF6)
        name.endsWith(".gradle") || name.endsWith(".gradle.kts") -> Color(0xFF02303A)
        name.endsWith(".xml") -> Color(0xFF00B4AB)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
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

fun sortContents(contents: List<Content>): List<Content> {
    return contents.sortedWith(compareBy<Content> { 
        when (it.type) {
            ContentType.DIR -> 0
            ContentType.FILE -> 1
            ContentType.SYMLINK -> 2
            ContentType.SUBMODULE -> 3
        }
    }.thenBy { it.name.lowercase() })
}