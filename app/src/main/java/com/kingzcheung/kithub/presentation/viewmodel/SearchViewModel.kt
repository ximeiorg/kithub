package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.IssueRepository
import com.kingzcheung.kithub.data.repository.RepositoryRepository
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Issue
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.domain.model.UserBrief
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val repositories: List<Repository> = emptyList(),
    val users: List<UserBrief> = emptyList(),
    val issues: List<Issue> = emptyList(),
    val loading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchType: SearchType = SearchType.REPOSITORIES,
    val page: Int = 1,
    val totalCount: Int = 0,
    val hasMore: Boolean = false
)

enum class SearchType {
    REPOSITORIES, USERS, ISSUES
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    private val userRepository: UserRepository,
    private val issueRepository: IssueRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()
    
    private companion object {
        const val PER_PAGE = 30
    }
    
    private var isLoadingMoreInProgress = false
    
    fun search(query: String) {
        if (query.isBlank()) {
            clearResults()
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(query = query, loading = true, error = null, page = 1) }
            try {
                when (_state.value.searchType) {
                    SearchType.REPOSITORIES -> {
                        val result = repositoryRepository.searchRepositories(query, 1)
                        _state.update {
                            it.copy(
                                repositories = result.items,
                                totalCount = result.totalCount,
                                hasMore = result.items.size < result.totalCount,
                                loading = false
                            )
                        }
                    }
                    SearchType.USERS -> {
                        val result = userRepository.searchUsers(query, 1)
                        _state.update {
                            it.copy(
                                users = result.items,
                                totalCount = result.totalCount,
                                hasMore = result.items.size < result.totalCount,
                                loading = false
                            )
                        }
                    }
                    SearchType.ISSUES -> {
                        val result = issueRepository.searchIssues(query, 1)
                        _state.update {
                            it.copy(
                                issues = result.items,
                                totalCount = result.totalCount,
                                hasMore = result.items.size < result.totalCount,
                                loading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Search failed") }
            }
        }
    }
    
    fun setSearchType(type: SearchType) {
        _state.update { 
            it.copy(
                searchType = type, 
                repositories = emptyList(), 
                users = emptyList(),
                issues = emptyList(),
                totalCount = 0,
                hasMore = false,
                error = null
            )
        }
        if (_state.value.query.isNotBlank()) {
            search(_state.value.query)
        }
    }
    
    fun loadMore() {
        val currentState = _state.value
        if (currentState.loading || 
            currentState.isLoadingMore || 
            currentState.query.isBlank() || 
            !currentState.hasMore ||
            isLoadingMoreInProgress) return
        
        isLoadingMoreInProgress = true
        
        viewModelScope.launch {
            val nextPage = currentState.page + 1
            _state.update { it.copy(page = nextPage, isLoadingMore = true, error = null) }
            
            try {
                when (currentState.searchType) {
                    SearchType.REPOSITORIES -> {
                        val result = repositoryRepository.searchRepositories(currentState.query, nextPage)
                        _state.update {
                            it.copy(
                                repositories = currentState.repositories + result.items,
                                hasMore = (currentState.repositories.size + result.items.size) < result.totalCount,
                                isLoadingMore = false
                            )
                        }
                    }
                    SearchType.USERS -> {
                        val result = userRepository.searchUsers(currentState.query, nextPage)
                        _state.update {
                            it.copy(
                                users = currentState.users + result.items,
                                hasMore = (currentState.users.size + result.items.size) < result.totalCount,
                                isLoadingMore = false
                            )
                        }
                    }
                    SearchType.ISSUES -> {
                        val result = issueRepository.searchIssues(currentState.query, nextPage)
                        _state.update {
                            it.copy(
                                issues = currentState.issues + result.items,
                                hasMore = (currentState.issues.size + result.items.size) < result.totalCount,
                                isLoadingMore = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to load more"
                val isRateLimitError = errorMessage.contains("403") || errorMessage.contains("rate limit")
                _state.update { 
                    it.copy(
                        isLoadingMore = false,
                        error = if (isRateLimitError) "Rate limit exceeded. Please wait before loading more." else errorMessage
                    )
                }
            } finally {
                isLoadingMoreInProgress = false
            }
        }
    }
    
    fun clearResults() {
        isLoadingMoreInProgress = false
        _state.update {
            SearchState(searchType = it.searchType)
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun refresh() {
        isLoadingMoreInProgress = false
        if (_state.value.query.isNotBlank()) {
            search(_state.value.query)
        }
    }
}