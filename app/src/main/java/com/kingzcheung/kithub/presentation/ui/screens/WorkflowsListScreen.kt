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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.Workflow
import com.kingzcheung.kithub.domain.model.WorkflowState
import com.kingzcheung.kithub.presentation.viewmodel.WorkflowsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowsListScreen(
    onNavigateToWorkflowRuns: (String) -> Unit = {},
    viewModel: WorkflowsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actions") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading && state.workflows.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.workflows.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = "No workflows",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No workflows found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add workflow files to .github/workflows/",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                        Text(
                            text = "${state.totalCount} workflow(s)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                
                items(
                    items = state.workflows,
                    key = { it.id }
                ) { workflow ->
                    WorkflowCard(
                        workflow = workflow,
                        onClick = { onNavigateToWorkflowRuns(workflow.id.toString()) }
                    )
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
fun WorkflowCard(
    workflow: Workflow,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = when (workflow.state) {
                            WorkflowState.ACTIVE -> Icons.Default.PlayArrow
                            WorkflowState.DISABLED, WorkflowState.DISABLED_MANUALLY -> Icons.Default.PlayDisabled
                        },
                        contentDescription = "Workflow state",
                        tint = when (workflow.state) {
                            WorkflowState.ACTIVE -> MaterialTheme.colorScheme.primary
                            WorkflowState.DISABLED, WorkflowState.DISABLED_MANUALLY -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = workflow.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = workflow.path,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (workflow.state) {
                        WorkflowState.ACTIVE -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        WorkflowState.DISABLED, WorkflowState.DISABLED_MANUALLY -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                ) {
                    Text(
                        text = workflow.state.name.lowercase().replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when (workflow.state) {
                            WorkflowState.ACTIVE -> MaterialTheme.colorScheme.primary
                            WorkflowState.DISABLED, WorkflowState.DISABLED_MANUALLY -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.Schedule,
                    contentDescription = "Created",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = formatDate(workflow.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    Icons.Outlined.Update,
                    contentDescription = "Updated",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = formatDate(workflow.updatedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )
        
        for (format in inputFormats) {
            try {
                val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                val date = sdf.parse(dateString)
                val outputSdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                return outputSdf.format(date ?: return dateString)
            } catch (e: Exception) {
                continue
            }
        }
        
        dateString.substring(0, 10)
    } catch (e: Exception) {
        dateString.substring(0, 10.coerceAtMost(dateString.length))
    }
}