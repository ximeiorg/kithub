package com.kingzcheung.kithub.presentation.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.Commit
import com.kingzcheung.kithub.domain.model.CommitFile
import com.kingzcheung.kithub.domain.model.CommitStats
import com.kingzcheung.kithub.presentation.viewmodel.CommitDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun CommitDetailScreen(
    onNavigateToFile: (String) -> Unit = {},
    viewModel: CommitDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.getCommits(context),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = strings.getBack(context))
                    }
                },
                actions = {
                    if (state.commit != null) {
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
                    Button(onClick = { viewModel.loadCommit() }) {
                        Text(strings.getRetry(context))
                    }
                }
            }
        } else if (state.commit != null) {
            CommitDetailContent(
                commit = state.commit!!,
                paddingValues = paddingValues,
                strings = strings,
                context = context
            )
        }
    }
}

@Composable
fun CommitDetailContent(
    commit: Commit,
    paddingValues: PaddingValues,
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
            CommitHeader(commit = commit)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            CommitAuthorInfo(commit = commit, strings = strings, context = context)
        }
        
        if (commit.stats != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                CommitStatsCard(stats = commit.stats, strings = strings, context = context)
            }
        }
        
        if (!commit.files.isNullOrEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = strings.getChangedFiles(context, commit.files.size),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(commit.files) { file ->
                CommitFileItem(file = file)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CommitHeader(commit: Commit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = commit.sha.take(7),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = commit.message,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CommitAuthorInfo(
    commit: Commit,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = strings.getAuthor(context),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.getAuthor(context),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://avatars.githubusercontent.com/u/0?email=${commit.author.email}",
                contentDescription = commit.author.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = commit.author.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = commit.author.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = strings.getCommitter(context),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.getCommitter(context),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://avatars.githubusercontent.com/u/0?email=${commit.committer.email}",
                contentDescription = commit.committer.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = commit.committer.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = commit.committer.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CommitStatsCard(
    stats: CommitStats,
    strings: com.kingzcheung.kithub.util.Strings,
    context: android.content.Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = strings.getAdditions(context),
            value = "+${stats.additions}",
            color = Color(0xFF238636)
        )
        StatCard(
            label = strings.getDeletions(context),
            value = "-${stats.deletions}",
            color = Color(0xFFDA3633)
        )
        StatCard(
            label = strings.getTotalChanges(context),
            value = "${stats.total}",
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun CommitFileItem(file: CommitFile) {
    val statusColor = when (file.status) {
        "added" -> Color(0xFF238636)
        "modified" -> Color(0xFFD29922)
        "removed" -> Color(0xFFDA3633)
        "renamed" -> Color(0xFF6F42C1)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val statusIcon = when (file.status) {
        "added" -> Icons.Default.Add
        "modified" -> Icons.Default.Edit
        "removed" -> Icons.Default.Delete
        "renamed" -> Icons.Default.DriveFileRenameOutline
        else -> Icons.Default.Description
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                statusIcon,
                contentDescription = file.status,
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.filename,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "+${file.additions}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF238636)
                    )
                    Text(
                        text = "-${file.deletions}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFDA3633)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = statusColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = file.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}