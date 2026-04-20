package com.kingzcheung.kithub.data.repository

import com.kingzcheung.kithub.data.remote.api.GitHubApi
import com.kingzcheung.kithub.data.remote.api.SearchResult
import com.kingzcheung.kithub.data.remote.dto.MarkReadRequest
import com.kingzcheung.kithub.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getCurrentUser(): User = api.getCurrentUser().toDomain()
    
    suspend fun getUser(username: String): User = api.getUser(username).toDomain()
    
    suspend fun getUserRepos(username: String, page: Int = 1): List<Repository> =
        api.getUserRepos(username, page).map { it.toDomain() }
    
    suspend fun getCurrentUserRepos(page: Int = 1): List<Repository> =
        api.getCurrentUserRepos(page).map { it.toDomain() }
    
    suspend fun getStarredRepos(username: String, page: Int = 1): List<Repository> =
        api.getStarredRepos(username, page).map { it.toDomain() }
    
    suspend fun getFollowers(username: String, page: Int = 1): List<UserBrief> =
        api.getFollowers(username, page).map { it.toDomain() }
    
    suspend fun getFollowing(username: String, page: Int = 1): List<UserBrief> =
        api.getFollowing(username, page).map { it.toDomain() }
    
    suspend fun getUserEvents(username: String, page: Int = 1): List<Event> =
        api.getUserEvents(username, page).map { it.toDomain() }
    
    suspend fun getCurrentUserIssues(page: Int = 1): List<Issue> =
        api.getCurrentUserIssues(page = page).map { it.toDomain() }
    
    suspend fun getCurrentUserOrgs(page: Int = 1): List<UserBrief> =
        api.getCurrentUserOrgs(page).map { it.toDomain() }
    
    suspend fun searchUsers(query: String, page: Int = 1): SearchResult<UserBrief> {
        val result = api.searchUsers(query, page = page)
        return SearchResult(
            totalCount = result.totalCount,
            incompleteResults = result.incompleteResults,
            items = result.items.map { it.toDomain() }
        )
    }
}

@Singleton
class RepositoryRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getRepository(owner: String, repo: String): Repository =
        api.getRepository(owner, repo).toDomain()
    
    suspend fun getBranches(owner: String, repo: String): List<Branch> =
        api.getBranches(owner, repo).map { it.toDomain() }
    
    suspend fun getContents(owner: String, repo: String, path: String, ref: String? = null): List<Content> =
        api.getContents(owner, repo, path, ref).map { it.toDomain() }
    
    suspend fun getFileContent(owner: String, repo: String, path: String, ref: String? = null): Content =
        api.getFileContent(owner, repo, path, ref).toDomain()
    
    suspend fun getReadme(owner: String, repo: String, ref: String? = null): Content =
        api.getReadme(owner, repo, ref).toDomain()
    
    suspend fun getLanguages(owner: String, repo: String): Map<String, Int> =
        api.getLanguages(owner, repo)
    
    suspend fun starRepo(owner: String, repo: String) = api.starRepo(owner, repo)
    
    suspend fun unstarRepo(owner: String, repo: String) = api.unstarRepo(owner, repo)
    
    suspend fun searchRepositories(query: String, page: Int = 1): SearchResult<Repository> {
        val result = api.searchRepositories(query, page = page)
        return SearchResult(
            totalCount = result.totalCount,
            incompleteResults = result.incompleteResults,
            items = result.items.map { it.toDomain() }
        )
    }
}

@Singleton
class IssueRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getIssues(owner: String, repo: String, state: String = "open", page: Int = 1): List<Issue> =
        api.getIssues(owner, repo, state, page).map { it.toDomain() }
    
    suspend fun getIssue(owner: String, repo: String, number: Int): Issue =
        api.getIssue(owner, repo, number).toDomain()
    
    suspend fun searchIssues(query: String, page: Int = 1): SearchResult<Issue> {
        val result = api.searchIssues(query, page = page)
        return SearchResult(
            totalCount = result.totalCount,
            incompleteResults = result.incompleteResults,
            items = result.items.map { it.toDomain() }
        )
    }
}

@Singleton
class PullRequestRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getPullRequests(owner: String, repo: String, state: String = "open", page: Int = 1): List<PullRequest> =
        api.getPullRequests(owner, repo, state, page).map { it.toDomain() }
    
    suspend fun getPullRequest(owner: String, repo: String, number: Int): PullRequest =
        api.getPullRequest(owner, repo, number).toDomain()
}

@Singleton
class CommitRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getCommits(owner: String, repo: String, sha: String? = null, page: Int = 1): List<CommitBrief> =
        api.getCommits(owner, repo, sha, page).map { it.toDomain() }
    
    suspend fun getCommit(owner: String, repo: String, sha: String): Commit =
        api.getCommit(owner, repo, sha).toDomain()
}

@Singleton
class NotificationRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getNotifications(
        all: Boolean = false,
        participating: Boolean = false,
        page: Int = 1
    ): List<NotificationThread> =
        api.getNotifications(all, participating, page).map { it.toDomain() }
    
    suspend fun markNotificationsAsRead(lastReadAt: String? = null) {
        api.markNotificationsAsRead(MarkReadRequest(lastReadAt))
    }
    
    suspend fun getNotificationThread(threadId: Int): NotificationThread =
        api.getNotificationThread(threadId).toDomain()
    
    suspend fun markThreadAsRead(threadId: Int) =
        api.markThreadAsRead(threadId)
    
    suspend fun markThreadAsDone(threadId: Int) =
        api.markThreadAsDone(threadId)
}

@Singleton
class WorkflowRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getWorkflows(owner: String, repo: String, page: Int = 1): WorkflowList =
        api.getWorkflows(owner, repo, page = page).toDomain()
    
    suspend fun getWorkflowRuns(
        owner: String,
        repo: String,
        workflowId: String,
        status: String? = null,
        page: Int = 1
    ): WorkflowRunList =
        api.getWorkflowRuns(owner, repo, workflowId, status, page = page).toDomain()
}

@Singleton
class ContributorRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun getContributors(
        owner: String,
        repo: String,
        anon: Boolean = false,
        page: Int = 1
    ): List<Contributor> =
        api.getContributors(owner, repo, if (anon) "1" else null, page = page).map { it.toDomain() }
}