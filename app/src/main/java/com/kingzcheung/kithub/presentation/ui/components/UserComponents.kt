package com.kingzcheung.kithub.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kingzcheung.kithub.domain.model.User
import com.kingzcheung.kithub.domain.model.UserBrief

@Composable
fun UserAvatar(
    avatarUrl: String,
    size: Int = 40,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = "User avatar",
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun UserCard(
    user: UserBrief,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(avatarUrl = user.avatarUrl, size = 48)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = user.login,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (user.type.isNotEmpty()) {
                Text(
                    text = user.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun UserProfileHeader(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserAvatar(avatarUrl = user.avatarUrl, size = 100)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = user.name ?: user.login,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = user.login,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        if (user.bio != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StatItem(label = "Followers", count = user.followers)
            StatItem(label = "Following", count = user.following)
            StatItem(label = "Repos", count = user.publicRepos)
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