package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.WorkflowRun
import com.kingzcheung.kithub.domain.model.WorkflowRunConclusion
import com.kingzcheung.kithub.domain.model.WorkflowRunStatus
import com.kingzcheung.kithub.presentation.viewmodel.WorkflowRunsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowRunsListScreen(
    viewModel: WorkflowRunsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = state.workflowName.ifEmpty { "Workflow Runs" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                viewModel.setStatusFilter(null)
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (state.statusFilter == null) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Completed") },
                            onClick = {
                                viewModel.setStatusFilter("completed")
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (state.statusFilter == "completed") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Running") },
                            onClick = {
                                viewModel.setStatusFilter("in_progress")
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (state.statusFilter == "in_progress") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Queued") },
                            onClick = {
                                viewModel.setStatusFilter("queued")
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                if (state.statusFilter == "queued") {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.workflowRuns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.workflowRuns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "No runs",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No workflow runs found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.totalCount > 0) {
                    item {
                        val filterText = when (state.statusFilter) {
                            "completed" -> "completed"
                            "in_progress" -> "running"
                            "queued" -> "queued"
                            null -> "total"
                            else -> state.statusFilter
                        }
                        Text(
                            text = "${state.totalCount} $filterText run(s)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                
                items(
                    items = state.workflowRuns,
                    key = { it.id }
                ) { run ->
                    WorkflowRunCard(run = run)
                }
                
                if (state.hasMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.loading) {
                                CircularProgressIndicator()
                            } else {
                                Button(onClick = { viewModel.loadMore() }) {
                                    Text("Load More")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkflowRunCard(
    run: WorkflowRun,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusIcon(
                            status = run.status,
                            conclusion = run.conclusion,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "#${run.runNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = run.displayTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = run.event,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        
                        Text(
                            text = run.headBranch,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                ConclusionBadge(
                    conclusion = run.conclusion,
                    status = run.status
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                run.triggeringActor?.let { actor ->
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Triggered by",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = actor.login,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Created",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = formatRelativeTime(run.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun StatusIcon(
    status: WorkflowRunStatus,
    conclusion: WorkflowRunConclusion?,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when {
        status == WorkflowRunStatus.IN_PROGRESS -> 
            Icons.Default.PlayCircleFilled to MaterialTheme.colorScheme.primary
        status == WorkflowRunStatus.QUEUED -> 
            Icons.Default.Schedule to MaterialTheme.colorScheme.onSurfaceVariant
        conclusion == WorkflowRunConclusion.SUCCESS -> 
            Icons.Default.CheckCircle to MaterialTheme.colorScheme.primary
        conclusion == WorkflowRunConclusion.FAILURE -> 
            Icons.Default.Error to MaterialTheme.colorScheme.error
        conclusion == WorkflowRunConclusion.CANCELLED -> 
            Icons.Default.Cancel to MaterialTheme.colorScheme.onSurfaceVariant
        conclusion == WorkflowRunConclusion.TIMED_OUT -> 
            Icons.Default.Timer to MaterialTheme.colorScheme.error
        conclusion == WorkflowRunConclusion.SKIPPED -> 
            Icons.Default.SkipNext to MaterialTheme.colorScheme.onSurfaceVariant
        else -> 
            Icons.Default.Circle to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Icon(
        imageVector = icon,
        contentDescription = "Status",
        tint = color,
        modifier = modifier
    )
}

@Composable
fun ConclusionBadge(
    conclusion: WorkflowRunConclusion?,
    status: WorkflowRunStatus
) {
    val (text, backgroundColor, textColor) = when {
        status == WorkflowRunStatus.IN_PROGRESS -> 
            Triple("Running", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        status == WorkflowRunStatus.QUEUED -> 
            Triple("Queued", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        conclusion == WorkflowRunConclusion.SUCCESS -> 
            Triple("Success", MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f), MaterialTheme.colorScheme.primary)
        conclusion == WorkflowRunConclusion.FAILURE -> 
            Triple("Failed", MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f), MaterialTheme.colorScheme.error)
        conclusion == WorkflowRunConclusion.CANCELLED -> 
            Triple("Cancelled", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        conclusion == WorkflowRunConclusion.TIMED_OUT -> 
            Triple("Timed Out", MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f), MaterialTheme.colorScheme.error)
        conclusion == WorkflowRunConclusion.SKIPPED -> 
            Triple("Skipped", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        else -> 
            Triple("Unknown", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun formatRelativeTime(dateString: String): String {
    return try {
        val inputFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        )
        
        for (format in inputFormats) {
            try {
                val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                val date = sdf.parse(dateString)
                val now = java.util.Date()
                val diffMs = now.time - (date?.time ?: now.time)
                val diffMinutes = diffMs / (60 * 1000)
                val diffHours = diffMinutes / 60
                val diffDays = diffHours / 24
                
                return when {
                    diffMinutes < 1 -> "just now"
                    diffMinutes < 60 -> "${diffMinutes}m ago"
                    diffHours < 24 -> "${diffHours}h ago"
                    diffDays < 7 -> "${diffDays}d ago"
                    else -> {
                        val outputSdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                        outputSdf.format(date ?: now)
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        dateString.substring(0, 10)
    } catch (e: Exception) {
        dateString.substring(0, 10.coerceAtMost(dateString.length))
    }
}