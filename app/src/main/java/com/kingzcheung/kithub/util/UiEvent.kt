package com.kingzcheung.kithub.util

sealed class UiEvent {
    data class ShowError(
        val message: String,
        val retryAction: (() -> Unit)? = null
    ) : UiEvent()
    
    data class ShowMessage(val message: String) : UiEvent()
}