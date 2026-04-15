package com.kingzcheung.kithub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kingzcheung.kithub.presentation.theme.KithubTheme
import com.kingzcheung.kithub.presentation.ui.screens.*
import com.kingzcheung.kithub.presentation.viewmodel.AuthViewModel
import com.kingzcheung.kithub.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KithubApp()
        }
    }
}

@Composable
fun KithubApp() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.settings.collectAsState()
    
    KithubTheme(
        themeMode = settings.themeMode,
        themeColor = settings.themeColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.state.collectAsState()
            
            NavHost(
                navController = navController,
                startDestination = if (authState.isAuthenticated) "main" else "auth",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(300)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(300)
                    )
                }
            ) {
                composable("auth") {
                    AuthScreen(
                        onAuthSuccess = {
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        viewModel = authViewModel
                    )
                }
                
                composable("main") {
                    MainScreen(
                        onNavigateToRepository = { owner, repo ->
                            navController.navigate("repo/$owner/$repo")
                        },
                        onNavigateToUser = { username ->
                            navController.navigate("user/$username")
                        },
                        onNavigateToIssue = { owner, repo, number ->
                            navController.navigate("issue/$owner/$repo/$number")
                        },
                        onNavigateToPullRequests = {
                            navController.navigate("pulls")
                        },
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    "user/{username}",
                    arguments = listOf(navArgument("username") { type = NavType.StringType })
                ) { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    UserScreen(
                        username = username,
                        onNavigateToRepository = { owner, repo ->
                            navController.navigate("repo/$owner/$repo")
                        }
                    )
                }
                
                composable(
                    "repo/{owner}/{repo}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repo = backStackEntry.arguments?.getString("repo") ?: ""
                    RepositoryScreen(
                        owner = owner,
                        repoName = repo,
                        onNavigateBack = { navController.navigateUp() },
                        onNavigateToIssues = { navController.navigate("issues/$owner/$repo") },
                        onNavigateToPullRequests = { navController.navigate("pulls/$owner/$repo") },
                        onNavigateToCommits = { navController.navigate("commits/$owner/$repo") },
                        onNavigateToUser = { username ->
                            navController.navigate("user/$username")
                        }
                    )
                }
                
                composable(
                    "issues/{owner}/{repo}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repo = backStackEntry.arguments?.getString("repo") ?: ""
                    IssuesListScreen(
                        onNavigateToIssue = { _, _, number ->
                            navController.navigate("issue/$owner/$repo/$number")
                        }
                    )
                }
                
                composable(
                    "pulls/{owner}/{repo}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repo = backStackEntry.arguments?.getString("repo") ?: ""
                    PullRequestsListScreen(
                        onNavigateToPullRequest = { _, _, number ->
                            navController.navigate("pr/$owner/$repo/$number")
                        }
                    )
                }
                
                composable(
                    "commits/{owner}/{repo}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    val repo = backStackEntry.arguments?.getString("repo") ?: ""
                    CommitsListScreen(
                        onNavigateToCommit = { sha ->
                            navController.navigate("commit/$owner/$repo/$sha")
                        }
                    )
                }
                
                composable(
                    "issue/{owner}/{repo}/{number}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType },
                        navArgument("number") { type = NavType.IntType }
                    )
                ) {
                    IssueDetailScreen(
                        onNavigateToUser = { username ->
                            navController.navigate("user/$username")
                        }
                    )
                }
                
                composable(
                    "pr/{owner}/{repo}/{number}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType },
                        navArgument("number") { type = NavType.IntType }
                    )
                ) {
                    PullRequestDetailScreen(
                        onNavigateToUser = { username ->
                            navController.navigate("user/$username")
                        }
                    )
                }
                
                composable(
                    "commit/{owner}/{repo}/{sha}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType },
                        navArgument("sha") { type = NavType.StringType }
                    )
                ) {
                    CommitDetailScreen()
                }
                
                composable(
                    "file/{owner}/{repo}/{path}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType },
                        navArgument("repo") { type = NavType.StringType },
                        navArgument("path") { type = NavType.StringType }
                    )
                ) {
                    FileViewerScreen(
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}