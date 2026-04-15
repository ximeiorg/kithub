package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.PullRequestRepository
import com.kingzcheung.kithub.domain.model.PullRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PullRequestDetailState(
    val pullRequest: PullRequest? = null,
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PullRequestDetailViewModel @Inject constructor(
    private val pullRequestRepository: PullRequestRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "PullRequestDetailViewModel"
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    private val number: Int = savedStateHandle["number"] ?: 0
    
    private val _state = MutableStateFlow(PullRequestDetailState())
    val state: StateFlow<PullRequestDetailState> = _state.asStateFlow()
    
    init {
        loadPullRequest()
    }
    
    fun loadPullRequest() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                Log.d(TAG, "Loading PR $owner/$repo#$number")
                val pr = pullRequestRepository.getPullRequest(owner, repo, number)
                Log.d(TAG, "PR loaded: ${pr.title}")
                _state.update { it.copy(pullRequest = pr, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading PR: ${e.message}", e)
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}