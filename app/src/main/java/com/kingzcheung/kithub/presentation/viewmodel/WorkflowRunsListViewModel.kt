package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.WorkflowRepository
import com.kingzcheung.kithub.domain.model.WorkflowRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkflowRunsListState(
    val workflowRuns: List<WorkflowRun> = emptyList(),
    val totalCount: Int = 0,
    val workflowName: String = "",
    val loading: Boolean = true,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val statusFilter: String? = null
)

@HiltViewModel
class WorkflowRunsListViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "WorkflowRunsListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    private val workflowId: String = savedStateHandle["workflowId"] ?: ""
    
    private val _state = MutableStateFlow(WorkflowRunsListState())
    val state: StateFlow<WorkflowRunsListState> = _state.asStateFlow()
    
    init {
        loadWorkflowRuns()
    }
    
    fun loadWorkflowRuns() {
        if (owner.isEmpty() || repo.isEmpty() || workflowId.isEmpty()) {
            _state.update { it.copy(loading = false, error = "Missing parameters") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, page = 1) }
            try {
                Log.d(TAG, "Loading workflow runs for $owner/$repo workflow $workflowId")
                val result = workflowRepository.getWorkflowRuns(
                    owner, repo, workflowId,
                    status = _state.value.statusFilter,
                    page = 1
                )
                Log.d(TAG, "Loaded ${result.workflowRuns.size} workflow runs")
                
                val workflowName = result.workflowRuns.firstOrNull()?.name ?: ""
                
                _state.update {
                    it.copy(
                        workflowRuns = result.workflowRuns,
                        totalCount = result.totalCount,
                        workflowName = workflowName,
                        loading = false,
                        hasMore = result.workflowRuns.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading workflow runs: ${e.message}", e)
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
                val result = workflowRepository.getWorkflowRuns(
                    owner, repo, workflowId,
                    status = _state.value.statusFilter,
                    page = nextPage
                )
                _state.update {
                    it.copy(
                        workflowRuns = it.workflowRuns + result.workflowRuns,
                        loading = false,
                        hasMore = result.workflowRuns.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more workflow runs: ${e.message}", e)
                _state.update { it.copy(loading = false) }
            }
        }
    }
    
    fun setStatusFilter(status: String?) {
        _state.update { it.copy(statusFilter = status) }
        loadWorkflowRuns()
    }
    
    fun refresh() {
        loadWorkflowRuns()
    }
}