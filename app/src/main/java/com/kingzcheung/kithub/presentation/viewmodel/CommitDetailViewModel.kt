package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.CommitRepository
import com.kingzcheung.kithub.domain.model.Commit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommitDetailState(
    val commit: Commit? = null,
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CommitDetailViewModel @Inject constructor(
    private val commitRepository: CommitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "CommitDetailViewModel"
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    private val sha: String = savedStateHandle["sha"] ?: ""
    
    private val _state = MutableStateFlow(CommitDetailState())
    val state: StateFlow<CommitDetailState> = _state.asStateFlow()
    
    init {
        loadCommit()
    }
    
    fun loadCommit() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                Log.d(TAG, "Loading commit $owner/$repo/$sha")
                val commit = commitRepository.getCommit(owner, repo, sha)
                Log.d(TAG, "Commit loaded: ${commit.message}")
                _state.update { it.copy(commit = commit, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading commit: ${e.message}", e)
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}