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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.LocalStrings
import com.kingzcheung.kithub.domain.model.UserBrief
import com.kingzcheung.kithub.presentation.ui.components.*
import com.kingzcheung.kithub.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun UserScreen(
    username: String,
    onNavigateToRepository: (String, String) -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.user?.login ?: strings.getProfile(context)) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = strings.getShare(context))
                    }
                }
            )
        }
    ) { padding ->
        if (state.loading && state.user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            state.user?.let { user ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        UserProfileHeader(user = user)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        user.bio?.let { bio ->
                            Card {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = strings.getBio(context),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = bio,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (user.company != null) {
                                IconText(
                                    icon = Icons.Default.Business,
                                    text = user.company
                                )
                            }
                            if (user.location != null) {
                                IconText(
                                    icon = Icons.Default.LocationOn,
                                    text = user.location
                                )
                            }
                        }
                        
                        if (user.blog != null && user.blog.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            IconText(
                                icon = Icons.Default.Link,
                                text = user.blog
                            )
                        }
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text(strings.getReposCount(context, user.publicRepos)) }
                            )
                            FilterChip(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1; viewModel.loadStarred() },
                                label = { Text(strings.getStarredTab(context)) }
                            )
                            FilterChip(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2; viewModel.loadFollowers() },
                                label = { Text(strings.getFollowersTab(context)) }
                            )
                            FilterChip(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3; viewModel.loadFollowing() },
                                label = { Text(strings.getFollowingTab(context)) }
                            )
                        }
                    }
                    
                    when (selectedTab) {
                        0 -> {
                            items(state.repos, key = { it.id }) { repo ->
                                RepositoryCard(
                                    repo = repo,
                                    onClick = { onNavigateToRepository(repo.owner.login, repo.name) }
                                )
                            }
                            
                            if (state.repos.size < user.publicRepos) {
                                item {
                                    Button(
                                        onClick = { viewModel.loadMoreRepos() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(strings.getLoadMore(context))
                                    }
                                }
                            }
                        }
                        1 -> {
                            items(state.starred, key = { it.id }) { repo ->
                                RepositoryCard(
                                    repo = repo,
                                    onClick = { onNavigateToRepository(repo.owner.login, repo.name) }
                                )
                            }
                        }
                        2 -> {
                            items(state.followers, key = { it.id }) { follower ->
                                UserCard(user = follower)
                            }
                        }
                        3 -> {
                            items(state.following, key = { it.id }) { following ->
                                UserCard(user = following)
                            }
                        }
                    }
                }
            }
        }
    }
}