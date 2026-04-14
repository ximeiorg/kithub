package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Event
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.domain.model.PullRequest
import com.kingzcheung.kithub.domain.model.PullRequestBranch
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.domain.model.User
import com.kingzcheung.kithub.domain.model.UserBrief
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: User? = null,
    val repos: List<Repository> = emptyList(),
    val starred: List<Repository> = emptyList(),
    val events: List<Event> = emptyList(),
    val issues: List<Issue> = emptyList(),
    val pullRequests: List<PullRequest> = emptyList(),
    val orgs: List<UserBrief> = emptyList(),
    val issuesError: String? = null,
    val prsError: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                Log.d(TAG, "Loading current user...")
                val user = userRepository.getCurrentUser()
                Log.d(TAG, "User loaded: ${user.login}")
                
                Log.d(TAG, "Loading user repos...")
                val repos = userRepository.getCurrentUserRepos(1)
                Log.d(TAG, "Repos loaded: ${repos.size}")
                
                Log.d(TAG, "Loading starred repos...")
                val starred = userRepository.getStarredRepos(user.login, 1)
                Log.d(TAG, "Starred loaded: ${starred.size}")
                
                Log.d(TAG, "Loading user events...")
                val events = userRepository.getUserEvents(user.login, 1)
                Log.d(TAG, "Events loaded: ${events.size}")
                
                Log.d(TAG, "Loading user orgs...")
                val orgs = userRepository.getCurrentUserOrgs(1)
                Log.d(TAG, "Orgs loaded: ${orgs.size}")
                
                _state.update {
                    it.copy(
                        user = user,
                        repos = repos.take(10),
                        starred = starred.take(10),
                        events = events.take(20),
                        orgs = orgs,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading home data: ${e.message}", e)
                _state.update { it.copy(loading = false, error = "${e.message}\n${e.cause?.message ?: ""}") }
            }
        }
    }
    
    fun loadIssues() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, issuesError = null) }
            try {
                Log.d(TAG, "Loading user issues with filter=all, state=all...")
                val allItems = userRepository.getCurrentUserIssues(1)
                Log.d(TAG, "Total items from /issues: ${allItems.size}")
                
                // Filter out pull requests - only show real issues
                val issues = allItems.filter { it.pullRequest == null }
                Log.d(TAG, "Filtered issues (excluding PRs): ${issues.size}")
                
                issues.forEach { issue ->
                    Log.d(TAG, "Issue #${issue.number}: ${issue.title}, state=${issue.state}, user=${issue.user?.login}, repo=${issue.repositoryUrl}")
                }
                
                if (issues.isEmpty() && allItems.isNotEmpty()) {
                    Log.w(TAG, "All items are pull requests, no actual issues found")
                }
                
                _state.update { it.copy(issues = issues, loading = false, issuesError = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading issues: ${e.message}", e)
                Log.e(TAG, "Error cause: ${e.cause?.message}", e.cause)
                _state.update { 
                    it.copy(
                        loading = false, 
                        issuesError = "${e.message}\nCause: ${e.cause?.message ?: "none"}"
                    ) 
                }
            }
        }
    }
    
    fun loadPullRequests() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, prsError = null) }
            try {
                Log.d(TAG, "Loading user pull requests from /issues endpoint...")
                val allItems = userRepository.getCurrentUserIssues(1)
                Log.d(TAG, "Total items from /issues: ${allItems.size}")
                
                // Filter pull requests - items with pullRequest field
                val prIssues = allItems.filter { it.pullRequest != null }
                Log.d(TAG, "Filtered PRs: ${prIssues.size}")
                
                // Convert Issue to PullRequest
                val prs = prIssues.map { issue ->
                    PullRequest(
                        id = issue.id,
                        nodeId = issue.nodeId,
                        number = issue.number,
                        title = issue.title,
                        body = issue.body,
                        bodyHtml = issue.bodyHtml,
                        user = issue.user,
                        state = issue.state,
                        htmlUrl = issue.pullRequest?.htmlUrl ?: issue.htmlUrl,
                        url = issue.url,
                        diffUrl = issue.pullRequest?.diffUrl,
                        patchUrl = issue.pullRequest?.patchUrl,
                        issueUrl = issue.url,
                        head = PullRequestBranch(label = "", ref = "", sha = ""),
                        base = PullRequestBranch(label = "", ref = "", sha = ""),
                        createdAt = issue.createdAt,
                        updatedAt = issue.updatedAt,
                        closedAt = issue.closedAt
                    )
                }
                
                prs.forEach { pr ->
                    Log.d(TAG, "PR #${pr.number}: ${pr.title}, state=${pr.state}, user=${pr.user.login}")
                }
                
                _state.update { it.copy(pullRequests = prs, loading = false, prsError = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading pull requests: ${e.message}", e)
                Log.e(TAG, "Error cause: ${e.cause?.message}", e.cause)
                _state.update { 
                    it.copy(
                        loading = false, 
                        prsError = "${e.message}\nCause: ${e.cause?.message ?: "none"}"
                    ) 
                }
            }
        }
    }
    
    fun loadOrgs() {
        viewModelScope.launch {
            if (_state.value.orgs.isNotEmpty()) return@launch
            _state.update { it.copy(loading = true) }
            try {
                Log.d(TAG, "Loading user orgs...")
                val orgs = userRepository.getCurrentUserOrgs(1)
                Log.d(TAG, "Orgs loaded: ${orgs.size}")
                _state.update { it.copy(orgs = orgs, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading orgs: ${e.message}", e)
                _state.update { it.copy(loading = false) }
            }
        }
    }
    
    fun refresh() {
        loadHomeData()
    }
}