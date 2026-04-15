package com.kingzcheung.kithub.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.kithub.data.repository.CommitRepository
import com.kingzcheung.kithub.data.repository.IssueRepository
import com.kingzcheung.kithub.data.repository.PullRequestRepository
import com.kingzcheung.kithub.data.repository.RepositoryRepository
import com.kingzcheung.kithub.domain.model.*
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
    val loading: Boolean = true,
    val error: String? = null,
    val isStarred: Boolean = false,
    val currentPath: String = ""
)

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    private val issueRepository: IssueRepository,
    private val pullRequestRepository: PullRequestRepository,
    private val commitRepository: CommitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val owner: String = savedStateHandle.get<String>("owner") ?: ""
    private val repo: String = savedStateHandle.get<String>("repo") ?: ""
    
    private val _state = MutableStateFlow(RepositoryState())
    val state: StateFlow<RepositoryState> = _state.asStateFlow()
    
    init {
        loadRepository()
    }
    
    fun loadRepository() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val repository = repositoryRepository.getRepository(owner, repo)
                val branches = repositoryRepository.getBranches(owner, repo)
                val contents = repositoryRepository.getContents(owner, repo, "", repository.defaultBranch)
                
                _state.update {
                    it.copy(
                        repository = repository,
                        branches = branches,
                        selectedBranch = repository.defaultBranch,
                        contents = contents,
                        loading = false
                    )
                }
                
                loadReadme()
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
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
                // README may not exist
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
                _state.update { it.copy(loading = false, error = e.message) }
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
                _state.update { it.copy(loading = false, error = e.message) }
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
                _state.update { it.copy(loading = false, error = e.message) }
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
                _state.update { it.copy(loading = false, error = e.message) }
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
                if (_state.value.isStarred) {
                    repositoryRepository.unstarRepo(owner, repo)
                    _state.update { it.copy(isStarred = false) }
                } else {
                    repositoryRepository.starRepo(owner, repo)
                    _state.update { it.copy(isStarred = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}