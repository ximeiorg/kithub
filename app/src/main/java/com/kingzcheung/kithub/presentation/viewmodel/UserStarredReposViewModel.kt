package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserStarredReposListState(
    val repos: List<Repository> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val sortBy: String = "created"
)

@HiltViewModel
class UserStarredReposViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    companion object {
        private const val PER_PAGE = 30
    }
    
    private val _state = MutableStateFlow(UserStarredReposListState())
    val state: StateFlow<UserStarredReposListState> = _state.asStateFlow()
    
    init {
        loadRepos()
    }
    
    fun loadRepos() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, page = 1) }
            try {
                val repos = userRepository.getCurrentUserStarredRepos(page = 1)
                _state.update {
                    it.copy(
                        repos = repos,
                        loading = false,
                        hasMore = repos.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
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
                val newRepos = userRepository.getCurrentUserStarredRepos(page = nextPage)
                _state.update {
                    it.copy(
                        repos = it.repos + newRepos,
                        loading = false,
                        hasMore = newRepos.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false) }
            }
        }
    }
    
    fun refresh() {
        loadRepos()
    }
    
    fun setSortBy(sort: String) {
        _state.update { it.copy(sortBy = sort) }
        loadRepos()
    }
}