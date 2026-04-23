package com.kingzcheung.kithub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.CommitRepository
import com.kingzcheung.kithub.data.repository.IssueRepository
import com.kingzcheung.kithub.data.repository.PullRequestRepository
import com.kingzcheung.kithub.data.repository.RepositoryRepository
import com.kingzcheung.kithub.domain.model.*
import com.kingzcheung.kithub.util.ErrorNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepositoryState(
    val repository: Repository? = null,
    val contents: List<Content> = emptyList(),
    val readme: String? = null,
    val issues: List<Issue> = emptyList(),
    val pullRequests: List<PullRequest> = emptyList(),
    val commits: List<CommitBrief> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val selectedBranch: String = "main",
    val languages: Map<String, Int> = emptyMap(),
    val loading: Boolean = true,
    val isStarred: Boolean = false,
    val currentPath: String = ""
)

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    private val issueRepository: IssueRepository,
    private val pullRequestRepository: PullRequestRepository,
    private val commitRepository: CommitRepository,
    private val errorNotifier: ErrorNotifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    companion object {
        private const val TAG = "RepositoryViewModel"
    }
    
    private val owner: String = savedStateHandle.get<String>("owner") ?: ""
    private val repo: String = savedStateHandle.get<String>("repo") ?: ""
    
    private val _state = MutableStateFlow(RepositoryState())
    val state: StateFlow<RepositoryState> = _state.asStateFlow()
    
    init {
        loadRepository()
    }
    
    fun loadRepository() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val repository = repositoryRepository.getRepository(owner, repo)
                val branches = repositoryRepository.getBranches(owner, repo)
                val contents = repositoryRepository.getContents(owner, repo, "", repository.defaultBranch)
                val languages = repositoryRepository.getLanguages(owner, repo)
                val isStarred = repositoryRepository.checkIfStarred(owner, repo)
                
                _state.update {
                    it.copy(
                        repository = repository,
                        branches = branches,
                        selectedBranch = repository.defaultBranch,
                        contents = contents,
                        languages = languages,
                        isStarred = isStarred,
                        loading = false
                    )
                }
                
                loadReadme()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading repository: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadRepository() }
            }
        }
    }
    
    fun loadReadme() {
        viewModelScope.launch {
            try {
                val readme = repositoryRepository.getReadme(owner, repo, _state.value.selectedBranch)
                val decodedContent = readme.content?.let { decodeBase64(it) }
                _state.update { it.copy(readme = decodedContent) }
            } catch (e: Exception) {
                Log.d(TAG, "README not found: ${e.message}")
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
    
    fun loadIssues(state: String = "open") {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val issues = issueRepository.getIssues(owner, repo, state, 1)
                _state.update { it.copy(issues = issues, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading issues: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadIssues(state) }
            }
        }
    }
    
    fun loadPullRequests(state: String = "open") {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val prs = pullRequestRepository.getPullRequests(owner, repo, state, 1)
                _state.update { it.copy(pullRequests = prs, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading pull requests: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadPullRequests(state) }
            }
        }
    }
    
    fun loadCommits() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val commits = commitRepository.getCommits(owner, repo, _state.value.selectedBranch, 1)
                _state.update { it.copy(commits = commits, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading commits: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { loadCommits() }
            }
        }
    }
    
    fun navigateToPath(path: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, currentPath = path) }
            try {
                val contents = repositoryRepository.getContents(owner, repo, path, _state.value.selectedBranch)
                _state.update { it.copy(contents = contents, loading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to path: ${e.message}", e)
                _state.update { it.copy(loading = false) }
                errorNotifier.showError(e.message ?: "Unknown error") { navigateToPath(path) }
            }
        }
    }
    
    fun selectBranch(branch: String) {
        _state.update { it.copy(selectedBranch = branch) }
        navigateToPath(_state.value.currentPath)
        loadReadme()
    }
    
    fun toggleStar() {
        viewModelScope.launch {
            try {
                val currentRepo = _state.value.repository
                if (_state.value.isStarred) {
                    repositoryRepository.unstarRepo(owner, repo)
                    _state.update { 
                        it.copy(
                            isStarred = false,
                            repository = currentRepo?.copy(
                                stargazersCount = currentRepo.stargazersCount - 1
                            )
                        )
                    }
                } else {
                    repositoryRepository.starRepo(owner, repo)
                    _state.update { 
                        it.copy(
                            isStarred = true,
                            repository = currentRepo?.copy(
                                stargazersCount = currentRepo.stargazersCount + 1
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling star: ${e.message}", e)
                errorNotifier.showError(e.message ?: "Unknown error") { toggleStar() }
            }
        }
    }
}