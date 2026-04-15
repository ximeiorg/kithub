package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.RepositoryRepository
import com.kingzcheung.kithub.domain.model.Content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class FileViewerState(
    val content: Content? = null,
    val fileContent: String? = null,
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FileViewerViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "FileViewerViewModel"
    }
    
    private val owner: String = savedStateHandle["owner"] ?: ""
    private val repo: String = savedStateHandle["repo"] ?: ""
    private val path: String = URLDecoder.decode(savedStateHandle["path"] ?: "", StandardCharsets.UTF_8.name())
    private val branch: String? = savedStateHandle["branch"]
    
    private val _state = MutableStateFlow(FileViewerState())
    val state: StateFlow<FileViewerState> = _state.asStateFlow()
    
    init {
        loadFile()
    }
    
    fun loadFile() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                Log.d(TAG, "Loading file $owner/$repo/$path")
                val file = repositoryRepository.getFileContent(owner, repo, path, branch)
                Log.d(TAG, "File loaded: ${file.name}")
                val decodedContent = file.content?.let { decodeBase64(it) }
                _state.update { 
                    it.copy(
                        content = file,
                        fileContent = decodedContent,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading file: ${e.message}", e)
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
    
    private fun decodeBase64(encoded: String): String {
        return try {
            val cleaned = encoded.replace("\n", "").trim()
            java.util.Base64.getDecoder().decode(cleaned).toString(Charsets.UTF_8)
        } catch (e: Exception) {
            encoded
        }
    }
}