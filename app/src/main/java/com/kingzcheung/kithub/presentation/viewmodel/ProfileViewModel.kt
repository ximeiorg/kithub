package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.Event
import com.kingzcheung.kithub.domain.model.Repository
import com.kingzcheung.kithub.domain.model.User
import com.kingzcheung.kithub.domain.model.UserBrief
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    val pinnedRepos: List<Repository> = emptyList(),
    val events: List<Event> = emptyList(),
    val orgs: List<UserBrief> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser()
                val repos = userRepository.getCurrentUserRepos(1)
                val pinnedRepos = repos.sortedByDescending { it.stargazersCount }.take(6)
                val events = userRepository.getUserEvents(user.login, 1)
                val orgs = userRepository.getCurrentUserOrgs(1)
                
                _state.update {
                    it.copy(
                        user = user,
                        pinnedRepos = pinnedRepos,
                        events = events,
                        orgs = orgs,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadMoreEvents() {
        val currentState = _state.value
        if (currentState.loading || currentState.user == null) return
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val newEvents = userRepository.getUserEvents(
                    currentState.user.login,
                    (currentState.events.size / 30) + 1
                )
                _state.update {
                    it.copy(
                        events = currentState.events + newEvents,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}