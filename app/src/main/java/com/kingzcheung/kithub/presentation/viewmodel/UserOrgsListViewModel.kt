package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.UserBrief
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserOrgsListState(
    val orgs: List<UserBrief> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
)

@HiltViewModel
class UserOrgsListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    companion object {
        private const val PER_PAGE = 30
    }
    
    private val _state = MutableStateFlow(UserOrgsListState())
    val state: StateFlow<UserOrgsListState> = _state.asStateFlow()
    
    init {
        loadOrgs()
    }
    
    fun loadOrgs() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, page = 1) }
            try {
                val orgs = userRepository.getCurrentUserOrgs(page = 1)
                _state.update {
                    it.copy(
                        orgs = orgs,
                        loading = false,
                        hasMore = orgs.size >= PER_PAGE,
                        page = 1
                    )
                }
            } catch (e: Exception) {
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
                val newOrgs = userRepository.getCurrentUserOrgs(page = nextPage)
                _state.update {
                    it.copy(
                        orgs = it.orgs + newOrgs,
                        loading = false,
                        hasMore = newOrgs.size >= PER_PAGE,
                        page = nextPage
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false) }
            }
        }
    }
    
    fun refresh() {
        loadOrgs()
    }
}