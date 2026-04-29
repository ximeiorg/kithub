package com.kingzcheung.kithub.presentation.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.NotificationRepository
import com.kingzcheung.kithub.domain.model.NotificationThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val notifications: List<NotificationThread> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val showAll: Boolean = false,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val actionError: String? = null,
    val actionSuccess: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    fun clearActionMessage() {
        _state.update { it.copy(actionError = null, actionSuccess = null) }
    }
    
    fun loadNotifications() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val notifications = notificationRepository.getNotifications(
                    all = _state.value.showAll,
                    page = 1
                )
                _state.update {
                    it.copy(
                        notifications = notifications,
                        loading = false,
                        page = 1,
                        hasMore = notifications.size >= 50
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun loadMoreNotifications() {
        val currentState = _state.value
        if (currentState.loading || !currentState.hasMore) return
        
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val newNotifications = notificationRepository.getNotifications(
                    all = currentState.showAll,
                    page = currentState.page + 1
                )
                _state.update {
                    it.copy(
                        notifications = currentState.notifications + newNotifications,
                        loading = false,
                        page = currentState.page + 1,
                        hasMore = newNotifications.size >= 50
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    fun toggleShowAll() {
        _state.update { it.copy(showAll = !it.showAll) }
        loadNotifications()
    }
    
    fun markAsRead(threadId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markThreadAsRead(threadId.toInt())
                _state.update {
                    it.copy(
                        notifications = it.notifications.map { n ->
                            if (n.id == threadId) n.copy(unread = false) else n
                        },
                        actionSuccess = "Marked as read"
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(actionError = e.message ?: "Failed to mark as read") }
            }
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                notificationRepository.markNotificationsAsRead()
                _state.update {
                    it.copy(
                        notifications = it.notifications.map { n -> n.copy(unread = false) },
                        actionSuccess = "All marked as read"
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(actionError = e.message ?: "Failed to mark all as read") }
            }
        }
    }
    
    fun markAsDone(threadId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markThreadAsDone(threadId.toInt())
                _state.update {
                    it.copy(
                        notifications = it.notifications.filter { n -> n.id != threadId },
                        actionSuccess = "Marked as done"
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(actionError = e.message ?: "Failed to mark as done") }
            }
        }
    }
}