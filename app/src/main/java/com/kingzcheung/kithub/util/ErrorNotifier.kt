package com.kingzcheung.kithub.util

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorNotifier @Inject constructor() {
    
    companion object {
        private const val TAG = "NetworkError"
    }
    
    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()
    
    fun showError(message: String, retryAction: (() -> Unit)? = null) {
        Log.e(TAG, "Network error: $message")
        _events.tryEmit(UiEvent.ShowError(message, retryAction))
    }
    
    fun showMessage(message: String) {
        Log.d(TAG, "Message: $message")
        _events.tryEmit(UiEvent.ShowMessage(message))
    }
}