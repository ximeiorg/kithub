package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.CommitRepository
import com.kingzcheung.kithub.domain.model.CommitBrief
import com.kingzcheung.kithub.util.ErrorNotifier
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
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class CommitsListViewModel @Inject constructor(
    private val commitRepository: CommitRepository,
    private val errorNotifier: ErrorNotifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "CommitsListViewModel"
    }
    
    private val owner: String = savedStateHandle.get<String>("owner") ?: ""
    private val repo: String = savedStateHandle.get<String>("repo") ?: ""
    
    private val _state = MutableStateFlow(CommitsListState())
    val state: StateFlow<CommitsListState> = _state.asStateFlow()
    
    init {
        loadCommits()
    }
    
    fun loadCommits() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
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
                Log.e(TAG, "Error loading commits: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadCommits() }
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
                Log.e(TAG, "Error loading more commits: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadMore() }
            }
        }
    }
    
    fun refresh() {
        loadCommits()
    }
}