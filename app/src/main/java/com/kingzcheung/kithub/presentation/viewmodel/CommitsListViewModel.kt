package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.CommitRepository
import com.kingzcheung.kithub.domain.model.CommitBrief
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommitsListState(
    val commits: List<CommitBrief> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class CommitsListViewModel @Inject constructor(
    private val commitRepository: CommitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val owner: String = savedStateHandle.get<String>("owner") ?: ""
    private val repo: String = savedStateHandle.get<String>("repo") ?: ""
    
    private val _state = MutableStateFlow(CommitsListState())
    val state: StateFlow<CommitsListState> = _state.asStateFlow()
    
    init {
        loadCommits()
    }
    
    fun loadCommits() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val commits = commitRepository.getCommits(owner, repo, null, 1)
                _state.update {
                    it.copy(
                        commits = commits,
                        loading = false,
                        page = 1,
                        hasMore = commits.size >= 30
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadMore() {
        val currentState = _state.value
        if (currentState.loading || !currentState.hasMore) return
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val newCommits = commitRepository.getCommits(owner, repo, null, currentState.page + 1)
                _state.update {
                    it.copy(
                        commits = currentState.commits + newCommits,
                        loading = false,
                        page = currentState.page + 1,
                        hasMore = newCommits.size >= 30
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun refresh() {
        loadCommits()
    }
}