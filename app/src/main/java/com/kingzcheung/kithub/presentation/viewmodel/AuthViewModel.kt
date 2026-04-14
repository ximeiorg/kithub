package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.AuthRepository
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.domain.model.AuthState
import com.kingzcheung.kithub.domain.model.DeviceCode
import com.kingzcheung.kithub.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    private val _deviceCode = MutableStateFlow<DeviceCode?>(null)
    val deviceCode: StateFlow<DeviceCode?> = _deviceCode.asStateFlow()
    
    private var pollingJob: Job? = null
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            val token = authRepository.getStoredToken()
            if (token != null) {
                _state.update { it.copy(isAuthenticated = true, token = token, loading = true) }
                try {
                    val user = userRepository.getCurrentUser()
                    _state.update { it.copy(user = user, loading = false) }
                } catch (e: Exception) {
                    _state.update { it.copy(loading = false, error = e.message) }
                }
            }
        }
    }
    
    fun startAuthFlow() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val code = authRepository.getDeviceCode()
                _deviceCode.value = code
                startPolling(code.deviceCode)
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    private fun startPolling(deviceCode: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            var interval = _deviceCode.value?.interval ?: 5
            var attempts = 0
            val maxAttempts = (_deviceCode.value?.expiresIn ?: 900) / interval
            
            while (isActive && attempts < maxAttempts) {
                delay(interval * 1000L)
                attempts++
                
                try {
                    val token = authRepository.getAccessToken(deviceCode)
                    if (token.error == null && token.accessToken.isNotEmpty()) {
                        authRepository.saveToken(token.accessToken)
                        val user = userRepository.getCurrentUser()
                        _state.update {
                            it.copy(
                                isAuthenticated = true,
                                token = token.accessToken,
                                user = user,
                                loading = false
                            )
                        }
                        _deviceCode.value = null
                        pollingJob?.cancel()
                        break
                    } else if (token.error == "authorization_pending") {
                        // User hasn't completed authorization yet, continue polling
                        continue
                    } else if (token.error == "slow_down") {
                        // Need to slow down polling
                        interval += 5
                        continue
                    } else if (token.error == "expired_token" || token.error == "access_denied") {
                        _state.update { it.copy(loading = false, error = token.errorDescription ?: token.error) }
                        _deviceCode.value = null
                        pollingJob?.cancel()
                        break
                    }
                } catch (e: Exception) {
                    // 422 errors are expected during polling, continue
                    val errorMsg = e.message ?: ""
                    if (errorMsg.contains("authorization_pending") || 
                        errorMsg.contains("slow_down") ||
                        errorMsg.contains("422")) {
                        // Expected errors, continue polling
                        if (errorMsg.contains("slow_down")) {
                            interval += 5
                        }
                        continue
                    }
                    // Unexpected error
                    _state.update { it.copy(loading = false, error = errorMsg) }
                    _deviceCode.value = null
                    pollingJob?.cancel()
                    break
                }
            }
            
            if (attempts >= maxAttempts) {
                _state.update { it.copy(loading = false, error = "Authorization timed out") }
                _deviceCode.value = null
            }
        }
    }
    
    fun cancelAuth() {
        pollingJob?.cancel()
        _deviceCode.value = null
        _state.update { it.copy(loading = false) }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.clearToken()
            _state.update { AuthState() }
        }
    }
}