package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.WorkflowRepository
import com.kingzcheung.kithub.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkflowsListState(
    val workflows: List<Workflow> = emptyList(),
    val totalCount: Int = 0,
    val loading: Boolean = true,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class WorkflowsListViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "WorkflowsListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    
    private val _state = MutableStateFlow(WorkflowsListState())
    val state: StateFlow<WorkflowsListState> = _state.asStateFlow()
    
    init {
        loadWorkflows()
    }
    
    fun loadWorkflows() {
        if (owner.isEmpty() || repo.isEmpty()) {
            _state.update { it.copy(loading = false, error = "Owner or repo not specified") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, page = 1) }
            try {
                Log.d(TAG, "Loading workflows for $owner/$repo")
                val result = workflowRepository.getWorkflows(owner, repo, page = 1)
                Log.d(TAG, "Loaded ${result.workflows.size} workflows, total: ${result.totalCount}")
                _state.update {
                    it.copy(
                        workflows = result.workflows,
                        totalCount = result.totalCount,
                        loading = false,
                        hasMore = result.workflows.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading workflows: ${e.message}", e)
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
                val result = workflowRepository.getWorkflows(owner, repo, page = nextPage)
                _state.update {
                    it.copy(
                        workflows = it.workflows + result.workflows,
                        loading = false,
                        hasMore = result.workflows.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more workflows: ${e.message}", e)
                _state.update { it.copy(loading = false) }
            }
        }
    }
    
    fun refresh() {
        loadWorkflows()
    }
}