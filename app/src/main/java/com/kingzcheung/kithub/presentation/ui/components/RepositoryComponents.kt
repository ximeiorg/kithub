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
import com.kingzcheung.kithub.domain.model.Repository

@Composable
fun RepositoryCard(
    repo: Repository,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(avatarUrl = repo.owner.avatarUrl, size = 32)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = repo.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (repo.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (repo.language != null) {
                    LanguageBadge(language = repo.language)
                }
                
                IconText(
                    icon = Icons.Default.Star,
                    text = repo.stargazersCount.toString()
                )
                
                IconText(
                    icon = Icons.Default.CallSplit,
                    text = repo.forksCount.toString()
                )
                
                if (repo.fork) {
                    Icon(
                        imageVector = Icons.Default.ForkRight,
                        contentDescription = "Forked",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageBadge(
    language: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = MaterialTheme.shapes.extraSmall,
            color = getLanguageColor(language)
        ) {}
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun IconText(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun getLanguageColor(language: String): androidx.compose.ui.graphics.Color {
    return when (language.lowercase()) {
        "kotlin" -> androidx.compose.ui.graphics.Color(0xFF7F52FF)
        "java" -> androidx.compose.ui.graphics.Color(0xFFB07219)
        "python" -> androidx.compose.ui.graphics.Color(0xFF3572A5)
        "javascript" -> androidx.compose.ui.graphics.Color(0xFFF1E05A)
        "typescript" -> androidx.compose.ui.graphics.Color(0xFF2B7489)
        "go" -> androidx.compose.ui.graphics.Color(0xFF00ADD8)
        "rust" -> androidx.compose.ui.graphics.Color(0xFFDEA584)
        "c" -> androidx.compose.ui.graphics.Color(0xFF555555)
        "c++" -> androidx.compose.ui.graphics.Color(0xFFF34B7D)
        "ruby" -> androidx.compose.ui.graphics.Color(0xFF701516)
        "swift" -> androidx.compose.ui.graphics.Color(0xFFFFAC45)
        "dart" -> androidx.compose.ui.graphics.Color(0xFF00B4AB)
        "flutter" -> androidx.compose.ui.graphics.Color(0xFF00B4AB)
        else -> androidx.compose.ui.graphics.Color.Gray
    }
}