package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.IssueRepository
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.util.ErrorNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssuesListState(
    val issues: List<Issue> = emptyList(),
    val stateFilter: String = "all",
    val loading: Boolean = false,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class IssuesListViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val errorNotifier: ErrorNotifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "IssuesListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val owner: String = savedStateHandle.get<String>("owner") ?: ""
    private val repo: String = savedStateHandle.get<String>("repo") ?: ""
    
    private val _state = MutableStateFlow(IssuesListState())
    val state: StateFlow<IssuesListState> = _state.asStateFlow()
    
    init {
        loadIssues()
    }
    
    fun loadIssues() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, page = 1) }
            try {
                Log.d(TAG, "Loading repository issues for $owner/$repo with state=${_state.value.stateFilter}")
                val issues = issueRepository.getIssues(owner, repo, _state.value.stateFilter, 1)
                val filteredIssues = issues.filter { it.pullRequest == null }
                Log.d(TAG, "Loaded ${filteredIssues.size} issues (excluding PRs)")
                _state.update {
                    it.copy(
                        issues = filteredIssues,
                        loading = false,
                        hasMore = filteredIssues.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading issues: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadIssues() }
            }
        }
    }
    
    fun loadMore() {
        if (_state.value.loading || !_state.value.hasMore) return
        viewModelScope.launch {
            val nextPage = _state.value.page + 1
            _state.update { it.copy(loading = true) }
            try {
                val issues = issueRepository.getIssues(owner, repo, _state.value.stateFilter, nextPage)
                val filteredIssues = issues.filter { it.pullRequest == null }
                _state.update {
                    it.copy(
                        issues = it.issues + filteredIssues,
                        loading = false,
                        hasMore = filteredIssues.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more issues: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadMore() }
            }
        }
    }
    
    fun setStateFilter(filter: String) {
        _state.update { it.copy(stateFilter = filter) }
        loadIssues()
    }
    
    fun refresh() {
        loadIssues()
    }
}