package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.IssueRepository
import com.kingzcheung.kithub.domain.model.Issue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssueDetailState(
    val issue: Issue? = null,
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IssueDetailViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "IssueDetailViewModel"
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    private val number: Int = savedStateHandle["number"] ?: 0
    
    private val _state = MutableStateFlow(IssueDetailState())
    val state: StateFlow<IssueDetailState> = _state.asStateFlow()
    
    init {
        loadIssue()
    }
    
    fun loadIssue() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                Log.d(TAG, "Loading issue $owner/$repo#$number")
                val issue = issueRepository.getIssue(owner, repo, number)
                Log.d(TAG, "Issue loaded: ${issue.title}")
                _state.update { it.copy(issue = issue, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading issue: ${e.message}", e)
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}