package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.domain.model.UserBrief
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.SearchType
import com.kingzcheung.kithub.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRepository: (String, String) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateToIssue: (String, String, Int) -> Unit = { _, _, _ -> },
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var searchQuery by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val debounceQuery = remember { MutableStateFlow("") }
    
    LaunchedEffect(searchQuery) {
        debounceQuery.value = searchQuery
    }
    
    LaunchedEffect(debounceQuery.debounce(300)) {
        if (debounceQuery.value.length >= 2) {
            viewModel.search(debounceQuery.value)
        } else if (debounceQuery.value.isEmpty()) {
            viewModel.clearResults()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            if (searchQuery.isNotEmpty()) {
                                viewModel.search(searchQuery)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        },
                        isFocused = isFocused,
                        interactionSource = interactionSource,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SearchTypeChips(
                        selectedType = state.searchType,
                        onTypeSelected = { viewModel.setSearchType(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            if (state.loading && state.repositories.isEmpty() && state.users.isEmpty() && state.issues.isEmpty()) {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            } else if (searchQuery.isEmpty()) {
                SearchEmptyState(modifier = Modifier.fillMaxSize())
            } else {
                SearchResultsList(
                    searchType = state.searchType,
                    repositories = state.repositories,
                    users = state.users,
                    issues = state.issues,
                    totalCount = state.totalCount,
                    hasMore = state.hasMore,
                    isLoadingMore = state.isLoadingMore,
                    onLoadMore = { viewModel.loadMore() },
                    onRepoClick = onNavigateToRepository,
                    onUserClick = onNavigateToUser,
                    onIssueClick = onNavigateToIssue,
                    error = state.error
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isFocused: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isFocused) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    Surface(
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                    singleLine = true,
                    interactionSource = interactionSource
                )
            }
            
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTypeChips(
    selectedType: SearchType,
    onTypeSelected: (SearchType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchChip(
            selected = selectedType == SearchType.REPOSITORIES,
            onClick = { onTypeSelected(SearchType.REPOSITORIES) },
            label = "Repos",
            icon = Icons.Outlined.Folder,
            modifier = Modifier.weight(1f)
        )
        SearchChip(
            selected = selectedType == SearchType.USERS,
            onClick = { onTypeSelected(SearchType.USERS) },
            label = "Users",
            icon = Icons.Outlined.Person,
            modifier = Modifier.weight(1f)
        )
        SearchChip(
            selected = selectedType == SearchType.ISSUES,
            onClick = { onTypeSelected(SearchType.ISSUES) },
            label = "Issues",
            icon = Icons.Outlined.BugReport,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
fun SearchEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Search your contents",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Search for repositories, users, or issues",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchResultsList(
    searchType: SearchType,
    repositories: List<Repository>,
    users: List<UserBrief>,
    issues: List<Issue>,
    totalCount: Int,
    hasMore: Boolean,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit,
    onRepoClick: (String, String) -> Unit,
    onUserClick: (String) -> Unit,
    onIssueClick: (String, String, Int) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    val items = when (searchType) {
        SearchType.REPOSITORIES -> repositories
        SearchType.USERS -> users
        SearchType.ISSUES -> issues
    }
    
    val displayCount = if (totalCount > 0) totalCount else items.size
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (items.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$displayCount results",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (items.size < displayCount) {
                        Text(
                            text = "${items.size} shown",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            when (searchType) {
                SearchType.REPOSITORIES -> {
                    items(repositories, key = { "repo_${it.id}" }) { repo ->
                        RepositoryCard(
                            repo = repo,
                            onClick = { onRepoClick(repo.owner.login, repo.name) }
                        )
                    }
                }
                SearchType.USERS -> {
                    items(users, key = { "user_${it.id}" }) { user ->
                        UserCard(
                            user = user,
                            onClick = { onUserClick(user.login) }
                        )
                    }
                }
                SearchType.ISSUES -> {
                    items(issues, key = { "issue_${it.id}" }) { issue ->
                        IssueSearchCard(
                            issue = issue,
                            onClick = { onIssueClick(issue.repositoryOwner, issue.repositoryName, issue.number) }
                        )
                    }
                }
            }
            
            if (hasMore) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = onLoadMore,
                            enabled = !isLoadingMore
                        ) {
                            if (isLoadingMore) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Loading...")
                            } else {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Load More")
                            }
                        }
                        
                        if (error != null && error.contains("rate limit")) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rate limit reached. Please wait a moment.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } else {
            item {
                EmptyState(
                    message = "No results found",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}