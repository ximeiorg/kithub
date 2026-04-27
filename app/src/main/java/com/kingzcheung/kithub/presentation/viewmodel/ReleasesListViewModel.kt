package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.ReleaseRepository
import com.kingzcheung.kithub.domain.model.Release
import com.kingzcheung.kithub.util.ErrorNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReleasesListState(
    val releases: List<Release> = emptyList(),
    val loading: Boolean = true,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class ReleasesListViewModel @Inject constructor(
    private val releaseRepository: ReleaseRepository,
    private val errorNotifier: ErrorNotifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "ReleasesListViewModel"
        private const val PER_PAGE = 30
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    
    private val _state = MutableStateFlow(ReleasesListState())
    val state: StateFlow<ReleasesListState> = _state.asStateFlow()
    
    init {
        loadReleases()
    }
    
    fun loadReleases() {
        if (owner.isEmpty() || repo.isEmpty()) {
            _state.update { it.copy(loading = false) }
            errorNotifier.showError("Missing parameters") { loadReleases() }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true, page = 1) }
            try {
                Log.d(TAG, "Loading releases for $owner/$repo")
                val result = releaseRepository.getReleases(owner, repo, page = 1)
                Log.d(TAG, "Loaded ${result.size} releases")
                
                _state.update {
                    it.copy(
                        releases = result,
                        loading = false,
                        hasMore = result.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading releases: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadReleases() }
            }
        }
    }
    
    fun loadMore() {
        if (_state.value.loading || !_state.value.hasMore) return
        viewModelScope.launch {
            val nextPage = _state.value.page + 1
            _state.update { it.copy(loading = true) }
            try {
                val result = releaseRepository.getReleases(owner, repo, page = nextPage)
                _state.update {
                    it.copy(
                        releases = it.releases + result,
                        loading = false,
                        hasMore = result.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading more releases: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadMore() }
            }
        }
    }
    
    fun refresh() {
        loadReleases()
    }
}