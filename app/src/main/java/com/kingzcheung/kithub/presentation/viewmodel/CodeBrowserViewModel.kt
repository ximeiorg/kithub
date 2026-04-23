package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.kingzcheung.kithub.data.repository.RepositoryRepository
import com.kingzcheung.kithub.domain.model.Content
import com.kingzcheung.kithub.domain.model.ContentType
import com.kingzcheung.kithub.util.ErrorNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class CodeBrowserState(
    val owner: String = "",
    val repo: String = "",
    val currentPath: String = "",
    val contents: List<Content> = emptyList(),
    val loading: Boolean = false
)

@HiltViewModel
class CodeBrowserViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    private val errorNotifier: ErrorNotifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "CodeBrowserViewModel"
    }
    
    private val _state = MutableStateFlow(CodeBrowserState())
    val state: StateFlow<CodeBrowserState> = _state.asStateFlow()
    
    private val owner: String = savedStateHandle.get<String>("owner") ?: ""
    private val repo: String = savedStateHandle.get<String>("repo") ?: ""
    private val initialPath: String = run {
        val rawPath = savedStateHandle.get<String>("path") ?: ""
        if (rawPath.isNotEmpty()) URLDecoder.decode(rawPath, StandardCharsets.UTF_8.name()) else ""
    }
    
    init {
        _state.update { it.copy(owner = owner, repo = repo, currentPath = initialPath) }
        loadContents(initialPath)
    }
    
    fun loadContents(path: String) {
        _state.update { it.copy(loading = true, currentPath = path) }
        
        viewModelScope.launch {
            try {
                val contents = repositoryRepository.getContents(owner, repo, path)
                    .sortedWith(compareBy<Content> { it.type != ContentType.DIR }.thenBy { it.name.lowercase() })
                
                _state.update { it.copy(contents = contents, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading contents: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Failed to load contents") { loadContents(path) }
            }
        }
    }
    
    fun navigateUp(): Boolean {
        val currentPath = _state.value.currentPath
        if (currentPath.isEmpty()) return false
        
        val parentPath = currentPath.substringBeforeLast('/', "")
        loadContents(parentPath)
        return true
    }
}