package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.ContributorRepository
import com.kingzcheung.kithub.domain.model.Contributor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContributorsListState(
    val contributors: List<Contributor> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val includeAnonymous: Boolean = false
)

@HiltViewModel
class ContributorsListViewModel @Inject constructor(
    private val contributorRepository: ContributorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "ContributorsListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    
    private val _state = MutableStateFlow(ContributorsListState())
    val state: StateFlow<ContributorsListState> = _state.asStateFlow()
    
    init {
        loadContributors()
    }
    
    fun loadContributors() {
        if (owner.isEmpty() || repo.isEmpty()) {
            _state.update { it.copy(loading = false, error = "Missing parameters") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, page = 1) }
            try {
                Log.d(TAG, "Loading contributors for $owner/$repo")
                val result = contributorRepository.getContributors(
                    owner, repo,
                    anon = _state.value.includeAnonymous,
                    page = 1
                )
                Log.d(TAG, "Loaded ${result.size} contributors")
                
                _state.update {
                    it.copy(
                        contributors = result,
                        loading = false,
                        hasMore = result.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading contributors: ${e.message}", e)
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
                val result = contributorRepository.getContributors(
                    owner, repo,
                    anon = _state.value.includeAnonymous,
                    page = nextPage
                )
                _state.update {
                    it.copy(
                        contributors = it.contributors + result,
                        loading = false,
                        hasMore = result.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more contributors: ${e.message}", e)
                _state.update { it.copy(loading = false) }
            }
        }
    }
    
    fun toggleAnonymous() {
        _state.update { it.copy(includeAnonymous = !it.includeAnonymous) }
        loadContributors()
    }
    
    fun refresh() {
        loadContributors()
    }
}