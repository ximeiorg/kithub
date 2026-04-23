package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.domain.model.PullRequest
import com.kingzcheung.kithub.domain.model.PullRequestBranch
import com.kingzcheung.kithub.util.ErrorNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PullRequestsListState(
    val pullRequests: List<PullRequest> = emptyList(),
    val stateFilter: String = "all",
    val loading: Boolean = false,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class PullRequestsListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val errorNotifier: ErrorNotifier
) : ViewModel() {
    
    companion object {
        private const val TAG = "PullRequestsListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val _state = MutableStateFlow(PullRequestsListState())
    val state: StateFlow<PullRequestsListState> = _state.asStateFlow()
    
    init {
        loadPullRequests()
    }
    
    fun loadPullRequests() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, page = 1) }
            try {
                Log.d(TAG, "Loading pull requests with state=${_state.value.stateFilter}")
                val allItems = userRepository.getCurrentUserIssues(page = 1)
                val prs = filterAndConvertPullRequests(allItems, _state.value.stateFilter)
                Log.d(TAG, "Loaded ${prs.size} pull requests")
                _state.update {
                    it.copy(
                        pullRequests = prs,
                        loading = false,
                        hasMore = prs.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading pull requests: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadPullRequests() }
            }
        }
    }
    
    fun loadMore() {
        if (_state.value.loading || !_state.value.hasMore) return
        viewModelScope.launch {
            val nextPage = _state.value.page + 1
            _state.update { it.copy(loading = true) }
            try {
                val allItems = userRepository.getCurrentUserIssues(page = nextPage)
                val newPrs = filterAndConvertPullRequests(allItems, _state.value.stateFilter)
                _state.update {
                    it.copy(
                        pullRequests = it.pullRequests + newPrs,
                        loading = false,
                        hasMore = newPrs.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more pull requests: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadMore() }
            }
        }
    }
    
    fun setStateFilter(filter: String) {
        _state.update { it.copy(stateFilter = filter) }
        loadPullRequests()
    }
    
    fun refresh() {
        loadPullRequests()
    }
    
    private fun filterAndConvertPullRequests(issues: List<Issue>, stateFilter: String): List<PullRequest> {
        val prIssues = issues.filter { it.pullRequest != null }
        
        val filtered = when (stateFilter) {
            "open" -> prIssues.filter { it.state.name.lowercase() == "open" }
            "closed" -> prIssues.filter { it.state.name.lowercase() == "closed" }
            else -> prIssues
        }
        
        return filtered.map { issue ->
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
                repositoryUrl = issue.repositoryUrl,
                head = PullRequestBranch(label = "", ref = "", sha = ""),
                base = PullRequestBranch(label = "", ref = "", sha = ""),
                createdAt = issue.createdAt,
                updatedAt = issue.updatedAt,
                closedAt = issue.closedAt
            )
        }
    }
}