package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
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

data class UserState(
    val user: User? = null,
    val repos: List<Repository> = emptyList(),
    val starred: List<Repository> = emptyList(),
    val followers: List<UserBrief> = emptyList(),
    val following: List<UserBrief> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val reposPage: Int = 1
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val username: String = savedStateHandle.get<String>("username") ?: ""
    
    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()
    
    init {
        loadUser()
    }
    
    fun loadUser() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val user = userRepository.getUser(username)
                val repos = userRepository.getUserRepos(username, 1)
                
                _state.update {
                    it.copy(
                        user = user,
                        repos = repos,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadMoreRepos() {
        val currentState = _state.value
        if (currentState.loading) return
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true, reposPage = currentState.reposPage + 1) }
            try {
                val newRepos = userRepository.getUserRepos(username, currentState.reposPage + 1)
                _state.update {
                    it.copy(
                        repos = currentState.repos + newRepos,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadStarred() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val starred = userRepository.getStarredRepos(username, 1)
                _state.update { it.copy(starred = starred, loading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadFollowers() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val followers = userRepository.getFollowers(username, 1)
                _state.update { it.copy(followers = followers, loading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadFollowing() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val following = userRepository.getFollowing(username, 1)
                _state.update { it.copy(following = following, loading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}