package com.kingzcheung.kithub

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kingzcheung.kithub.data.store.AppLanguage
import com.kingzcheung.kithub.presentation.theme.KithubTheme
import com.kingzcheung.kithub.presentation.ui.screens.*
import com.kingzcheung.kithub.presentation.viewmodel.AuthViewModel
import com.kingzcheung.kithub.presentation.viewmodel.SettingsViewModel
import com.kingzcheung.kithub.util.ErrorNotifier
import com.kingzcheung.kithub.data.store.SettingsStore
import com.kingzcheung.kithub.util.LocaleHelper
import com.kingzcheung.kithub.util.Strings
import com.kingzcheung.kithub.util.UiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

val LocalStrings = staticCompositionLocalOf<Strings> { error("Strings not provided") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private var currentLanguage: AppLanguage = AppLanguage.SYSTEM
    
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val langOrdinal = prefs.getInt("app_language", AppLanguage.SYSTEM.ordinal)
        val language = AppLanguage.values().getOrElse(langOrdinal) { AppLanguage.SYSTEM }
        currentLanguage = language
        val context = LocaleHelper.wrapContext(newBase, language)
        super.attachBaseContext(context)
    }
    
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
    val errorNotifier: ErrorNotifier = hiltViewModel<SettingsViewModel>().errorNotifier
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val strings = remember(settings.appLanguage) {
        Strings
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var pendingRetryAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    
    val errorEvent by errorNotifier.events.collectAsState(initial = null)
    
    LaunchedEffect(errorEvent) {
        scope.launch {
            errorEvent?.let { event ->
                when (event) {
                    is UiEvent.ShowError -> {
                        pendingRetryAction = event.retryAction
                        val result = snackbarHostState.showSnackbar(
                            message = strings.getNetworkError(context),
                            actionLabel = if (event.retryAction != null) strings.getRetry(context) else null,
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            pendingRetryAction?.invoke()
                        }
                        pendingRetryAction = null
                    }
                    is UiEvent.ShowMessage -> {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
    
    CompositionLocalProvider(LocalStrings provides strings) {
        KithubTheme(
            themeMode = settings.themeMode,
            themeColor = settings.themeColor
        ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.state.collectAsState()
                
                NavHost(
                    navController = navController,
                    startDestination = if (authState.isAuthenticated) "main" else "auth",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
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
                            onNavigateToCode = { navController.navigate("code/$owner/$repo") },
                            onNavigateToActions = { navController.navigate("actions/$owner/$repo") },
                            onNavigateToContributors = { navController.navigate("contributors/$owner/$repo") },
                            onNavigateToReleases = { navController.navigate("releases/$owner/$repo") },
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
                        "actions/{owner}/{repo}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val owner = backStackEntry.arguments?.getString("owner") ?: ""
                        val repo = backStackEntry.arguments?.getString("repo") ?: ""
                        WorkflowsListScreen(
                            onNavigateToWorkflowRuns = { workflowId ->
                                navController.navigate("workflow_runs/$owner/$repo/$workflowId")
                            }
                        )
                    }
                    
                    composable(
                        "contributors/{owner}/{repo}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType }
                        )
                    ) {
                        ContributorsListScreen(
                            onNavigateToUser = { username ->
                                navController.navigate("user/$username")
                            }
                        )
                    }
                    
                    composable(
                        "releases/{owner}/{repo}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType }
                        )
                    ) {
                        ReleasesListScreen(
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }
                    
                    composable(
                        "workflow_runs/{owner}/{repo}/{workflowId}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType },
                            navArgument("workflowId") { type = NavType.StringType }
                        )
                    ) {
                        WorkflowRunsListScreen()
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
                        "code/{owner}/{repo}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val owner = backStackEntry.arguments?.getString("owner") ?: ""
                        val repo = backStackEntry.arguments?.getString("repo") ?: ""
                        CodeBrowserScreen(
                            onNavigateBack = { navController.navigateUp() },
                            onNavigateToFile = { path ->
                                val encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8.name())
                                navController.navigate("file/$owner/$repo/$encodedPath")
                            }
                        )
                    }
                    
                    composable(
                        "code/{owner}/{repo}/{path}",
                        arguments = listOf(
                            navArgument("owner") { type = NavType.StringType },
                            navArgument("repo") { type = NavType.StringType },
                            navArgument("path") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val owner = backStackEntry.arguments?.getString("owner") ?: ""
                        val repo = backStackEntry.arguments?.getString("repo") ?: ""
                        CodeBrowserScreen(
                            onNavigateBack = { navController.navigateUp() },
                            onNavigateToFile = { path ->
                                val encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8.name())
                                navController.navigate("file/$owner/$repo/$encodedPath")
                            }
                        )
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
}
}