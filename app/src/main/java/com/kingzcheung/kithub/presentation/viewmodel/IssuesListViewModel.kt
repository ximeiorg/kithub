package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Issue
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
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class IssuesListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "IssuesListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val _state = MutableStateFlow(IssuesListState())
    val state: StateFlow<IssuesListState> = _state.asStateFlow()
    
    init {
        loadIssues()
    }
    
    fun loadIssues() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, page = 1) }
            try {
                Log.d(TAG, "Loading issues with state=${_state.value.stateFilter}")
                val allItems = userRepository.getCurrentUserIssues(page = 1)
                val issues = filterIssues(allItems, _state.value.stateFilter)
                Log.d(TAG, "Loaded ${issues.size} issues")
                _state.update {
                    it.copy(
                        issues = issues,
                        loading = false,
                        hasMore = issues.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading issues: ${e.message}", e)
                _state.update { it.copy(loading = false, error = e.message) }
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
                val newIssues = filterIssues(allItems, _state.value.stateFilter)
                _state.update {
                    it.copy(
                        issues = it.issues + newIssues,
                        loading = false,
                        hasMore = newIssues.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more issues: ${e.message}", e)
                _state.update { it.copy(loading = false) }
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
    
    private fun filterIssues(issues: List<Issue>, stateFilter: String): List<Issue> {
        return when (stateFilter) {
            "open" -> issues.filter { it.state.name.lowercase() == "open" }
            "closed" -> issues.filter { it.state.name.lowercase() == "closed" }
            else -> issues
        }
    }
}