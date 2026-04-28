package com.kingzcheung.kithub.presentation.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.outlined.CallSplit
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.Branch
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.ui.components.MarkwonText
import com.kingzcheung.kithub.presentation.viewmodel.RepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun RepositoryScreen(
    owner: String,
    repoName: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToIssues: () -> Unit = {},
    onNavigateToPullRequests: () -> Unit = {},
    onNavigateToCommits: () -> Unit = {},
    onNavigateToCode: () -> Unit = {},
    onNavigateToActions: () -> Unit = {},
    onNavigateToContributors: () -> Unit = {},
    onNavigateToReleases: () -> Unit = {},
    onNavigateToUser: (String) -> Unit = {},
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showBranchSelector by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val strings = LocalStrings.current
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = strings.getBack(context))
                    }
                },
                actions = {
                    if (state.repository != null) {
                        IconButton(onClick = { /* TODO: Add new issue */ }) {
                            Icon(Icons.Default.Add, contentDescription = strings.getAddIssue(context))
                        }
                        IconButton(onClick = { viewModel.toggleStar() }) {
                            Icon(
                                imageVector = if (state.isStarred) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = strings.getStar(context),
                                tint = if (state.isStarred) Color(0xFFFFA726) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = strings.getShare(context))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
        }
    ) { padding ->
        if (state.loading && state.repository == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.repository != null) {
            val repo = state.repository!!
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.clickable { onNavigateToUser(repo.owner.login) }
                                ) {
                                    UserAvatar(
                                        avatarUrl = repo.owner.avatarUrl,
                                        size = 40
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = repo.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (repo.description != null) {
                                        Text(
                                            text = repo.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconText(
                                    icon = Icons.Rounded.Star,
                                    text = "${repo.stargazersCount}",
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                                IconText(
                                    icon = Icons.AutoMirrored.Filled.CallSplit,
                                    text = "${repo.forksCount}",
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                                IconText(
                                    icon = Icons.Default.ErrorOutline,
                                    text = "${repo.openIssuesCount}",
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                            }
                            
                            repo.license?.let { license ->
                                Spacer(modifier = Modifier.height(6.dp))
                                IconText(
                                    icon = Icons.Default.Gavel,
                                    text = license.name,
                                    textStyle = MaterialTheme.typography.labelSmall
                                )
                            }
                            
                            if (state.languages.isNotEmpty()) {
                                val totalBytes = state.languages.values.sum()
                                val sortedLanguages = state.languages.entries.sortedByDescending { it.value }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                ) {
                                    sortedLanguages.forEach { (language, bytes) ->
                                        val percentage = (bytes.toFloat() / totalBytes.toFloat())
                                        val color = getLanguageColor(language)
                                        
                                        Box(
                                            modifier = Modifier
                                                .weight(percentage)
                                                .fillMaxHeight()
                                                .background(color)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    sortedLanguages.take(5).forEach { (language, bytes) ->
                                        val percentage = (bytes.toFloat() / totalBytes.toFloat() * 100).toInt()
                                        val color = getLanguageColor(language)
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(color)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "$language $percentage%",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.ErrorOutline,
                        title = strings.getIssues(context),
                        count = repo.openIssuesCount,
                        onClick = onNavigateToIssues
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.AutoMirrored.Outlined.CallSplit,
                        title = strings.getPulls(context),
                        onClick = onNavigateToPullRequests
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.PlayArrow,
                        title = strings.getActions(context),
                        onClick = onNavigateToActions
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.People,
                        title = strings.getContributors(context),
                        onClick = onNavigateToContributors
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.NewReleases,
                        title = strings.getReleases(context),
                        onClick = onNavigateToReleases
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        onClick = { showBranchSelector = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Source,
                                contentDescription = strings.getBranch(context),
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.selectedBranch,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.UnfoldMore,
                                contentDescription = strings.getChangeBranch(context),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.Folder,
                        title = strings.getCode(context),
                        onClick = onNavigateToCode
                    )
                }
                
                item {
                    RepositoryMenuItem(
                        icon = Icons.Outlined.History,
                        title = strings.getCommits(context),
                        onClick = onNavigateToCommits
                    )
                }
                
                if (state.readme != null) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = strings.getReadme(context),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    item {
                        MarkwonText(
                            markdown = state.readme!!,
                            owner = state.repository!!.owner.login,
                            repo = state.repository!!.name,
                            branch = state.selectedBranch,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
    
    if (showBranchSelector) {
        BranchSelectorDialog(
            branches = state.branches,
            selectedBranch = state.selectedBranch,
            onBranchSelected = { branch ->
                viewModel.selectBranch(branch)
                showBranchSelector = false
            },
            onDismiss = { showBranchSelector = false },
            context = context,
            strings = strings
        )
    }
}

@Composable
fun RepositoryMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    count: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (count != null && count > 0) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}

fun getLanguageColor(language: String): Color {
    return when (language.lowercase()) {
        "kotlin" -> Color(0xFFA97BFF)
        "java" -> Color(0xFFB07219)
        "python" -> Color(0xFF3572A5)
        "javascript" -> Color(0xFFF1E05A)
        "typescript" -> Color(0xFF2B7489)
        "c" -> Color(0xFF555555)
        "c++" -> Color(0xFFF34B7D)
        "c#" -> Color(0xFF178600)
        "go" -> Color(0xFF00ADD8)
        "rust" -> Color(0xFFDEA584)
        "swift" -> Color(0xFFFF563D)
        "objective-c" -> Color(0xFF438EFF)
        "ruby" -> Color(0xFF701516)
        "php" -> Color(0xFF4F5D95)
        "scala" -> Color(0xFFC22D40)
        "dart" -> Color(0xFF00B4AB)
        "flutter" -> Color(0xFF00B4AB)
        "html" -> Color(0xFFE44B23)
        "css" -> Color(0xFF563D7C)
        "shell" -> Color(0xFF89E051)
        "bash" -> Color(0xFF89E051)
        "json" -> Color(0xFF292929)
        "yaml" -> Color(0xFFCB171E)
        "markdown" -> Color(0xFF083FA1)
        "sql" -> Color(0xFFE38C00)
        else -> Color(0xFFCCCCCC)
    }
}

@Composable
fun BranchSelectorDialog(
    branches: List<Branch>,
    selectedBranch: String,
    onBranchSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    context: android.content.Context,
    strings: com.kingzcheung.kithub.util.Strings
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = strings.getSelectBranch(context),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            LazyColumn {
                items(branches, key = { it.name }) { branch ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBranchSelected(branch.name) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (branch.name == selectedBranch) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = strings.getSelected(context),
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = branch.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Spacer(modifier = Modifier.width(28.dp))
                            Text(
                                text = branch.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.getClose(context))
            }
        }
    )
}