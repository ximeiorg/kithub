package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.RepositoryRepository
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Event
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreState(
    val user: User? = null,
    val events: List<Event> = emptyList(),
    val repoDetails: Map<String, Repository> = emptyMap(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 1
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()
    
    init {
        loadEvents()
    }
    
    fun loadEvents() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser()
                val events = userRepository.getReceivedEvents(user.login, 1)
                
                _state.update {
                    it.copy(
                        user = user,
                        events = events,
                        repoDetails = emptyMap(),
                        loading = false,
                        page = 1
                    )
                }
                
                loadRepoDetailsForStarEvents(events)
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun refresh() {
        loadEvents()
    }
    
    fun loadMoreEvents() {
        val currentState = _state.value
        if (currentState.loadingMore || currentState.user == null) return
        
        viewModelScope.launch {
            _state.update { it.copy(loadingMore = true) }
            try {
                val nextPage = currentState.page + 1
                val newEvents = userRepository.getReceivedEvents(
                    currentState.user.login,
                    nextPage
                )
                _state.update {
                    it.copy(
                        events = currentState.events + newEvents,
                        loadingMore = false,
                        page = nextPage
                    )
                }
                
                loadRepoDetailsForStarEvents(newEvents)
            } catch (e: Exception) {
                _state.update { it.copy(loadingMore = false) }
            }
        }
    }
    
    private fun loadRepoDetailsForStarEvents(events: List<Event>) {
        val starEvents = events.filter { it.type.name == "WatchEvent" && it.repo != null }
        if (starEvents.isEmpty()) return
        
        viewModelScope.launch {
            starEvents.forEach { event ->
                event.repo?.name?.let { repoName ->
                    val parts = repoName.split("/")
                    if (parts.size == 2 && _state.value.repoDetails[repoName] == null) {
                        try {
                            val repo = repositoryRepository.getRepository(parts[0], parts[1])
                            _state.update { it.copy(repoDetails = it.repoDetails + (repoName to repo)) }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
    }
}