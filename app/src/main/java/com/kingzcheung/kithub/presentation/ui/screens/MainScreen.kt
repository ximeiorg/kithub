package com.kingzcheung.kithub.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Explore
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.TwoTone.Home)
    object Notifications : BottomNavItem("notifications", "Notifications", Icons.TwoTone.Notifications)
    object Explore : BottomNavItem("explore", "Explore", Icons.TwoTone.Explore)
    object Profile : BottomNavItem("profile", "Profile", Icons.TwoTone.Person)
    object Settings : BottomNavItem("settings", "Settings", Icons.TwoTone.Settings)
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    onNavigateToRepository: (String, String) -> Unit,
    onNavigateToUser: (String) -> Unit,
    onNavigateToIssue: (String, String, Int) -> Unit,
    onLogout: () -> Unit
) {
    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Notifications,
        BottomNavItem.Explore,
        BottomNavItem.Profile
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val shouldShowBottomBar = currentRoute in navItems.map { it.route }
    
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    items = navItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationRoute ?: "home") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToRepository = onNavigateToRepository,
                    onNavigateToUser = onNavigateToUser,
                    onNavigateToIssues = { navController.navigate("issues") },
                    onNavigateToPullRequests = { navController.navigate("prs") },
                    onNavigateToSearch = { navController.navigate("search") }
                )
            }
            composable("search") {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRepository = onNavigateToRepository,
                    onNavigateToUser = onNavigateToUser,
                    onNavigateToIssue = onNavigateToIssue
                )
            }
            composable(BottomNavItem.Notifications.route) {
                NotificationsScreen()
            }
            composable(BottomNavItem.Explore.route) {
                ExploreScreen(
                    onNavigateToUser = onNavigateToUser,
                    onNavigateToRepository = onNavigateToRepository
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToUser = onNavigateToUser,
                    onNavigateToRepository = onNavigateToRepository,
                    onNavigateToRepos = { navController.navigate("user_repos") },
                    onNavigateToOrgs = { navController.navigate("user_orgs") },
                    onNavigateToStarred = { navController.navigate("user_starred") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("user_repos") {
                UserReposListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRepository = onNavigateToRepository
                )
            }
            composable("user_orgs") {
                UserOrgsListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToUser = onNavigateToUser
                )
            }
            composable("user_starred") {
                UserStarredReposScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRepository = onNavigateToRepository
                )
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(onLogout = onLogout)
            }
            composable("issues") {
                IssuesListScreen(
                    onNavigateToIssue = onNavigateToIssue
                )
            }
            composable("prs") {
                PullRequestsListScreen(
                    onNavigateToPullRequest = onNavigateToIssue
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Column {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    
                    BottomNavItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val transition = updateTransition(
        targetState = isSelected,
        label = "navItemTransition"
    )
    
    val iconColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 200) },
        label = "iconColor"
    ) { selected ->
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val backgroundColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 200) },
        label = "bgColor"
    ) { selected ->
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else Color.Transparent
    }
    
    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "contentAlpha"
    ) { selected ->
        if (selected) 1f else 0.7f
    }
    
    val scale by transition.animateFloat(
        transitionSpec = { spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ) },
        label = "scale"
    ) { selected ->
        if (selected) 1.05f else 1f
    }
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .height(44.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .alpha(contentAlpha)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(200)) + 
                        expandHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            expandFrom = Alignment.Start
                        ),
                exit = fadeOut(animationSpec = tween(200)) + 
                       shrinkHorizontally(
                           animationSpec = spring(
                               dampingRatio = Spring.DampingRatioMediumBouncy,
                               stiffness = Spring.StiffnessLow
                           ),
                           shrinkTowards = Alignment.End
                       )
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = iconColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

