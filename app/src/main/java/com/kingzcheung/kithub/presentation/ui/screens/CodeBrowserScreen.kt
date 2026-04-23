package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.Content
import com.kingzcheung.kithub.domain.model.ContentType
import com.kingzcheung.kithub.presentation.viewmodel.CodeBrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBrowserScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFile: (String) -> Unit,
    viewModel: CodeBrowserViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${state.owner}/${state.repo}",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (state.currentPath.isNotEmpty()) {
                            Text(
                                text = state.currentPath,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!viewModel.navigateUp()) {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (state.loading && state.contents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(state.contents, key = { it.path }) { content ->
                    ContentItem(
                        content = content,
                        onClick = {
                            if (content.type == ContentType.DIR) {
                                viewModel.loadContents(content.path)
                            } else {
                                onNavigateToFile(content.path)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (content.type == ContentType.DIR) Icons.Outlined.Folder
                             else Icons.Outlined.Description,
                contentDescription = content.type.name,
                modifier = Modifier.size(24.dp),
                tint = if (content.type == ContentType.DIR) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = content.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (content.type == ContentType.FILE) {
                    Text(
                        text = formatFileSize(content.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (content.type == ContentType.DIR) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

fun formatFileSize(size: Int): String {
    val kb = size / 1024
    return if (kb < 1024) {
        "$kb KB"
    } else {
        val mb = kb / 1024
        "$mb MB"
    }
}