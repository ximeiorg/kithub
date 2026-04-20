package com.kingzcheung.kithub.data.remote.api

import com.kingzcheung.kithub.data.remote.dto.*
import retrofit2.http.*

interface GitHubApi {
    @GET("user")
    suspend fun getCurrentUser(): UserDto
    
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): UserDto
    
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated",
        @Query("type") type: String = "owner"
    ): List<RepositoryDto>
    
    @GET("user/repos")
    suspend fun getCurrentUserRepos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated",
        @Query("affiliation") affiliation: String = "owner,collaborator,organization_member"
    ): List<RepositoryDto>
    
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepositoryDto
    
    @GET("repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<BranchDto>
    
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Query("ref") ref: String? = null
    ): List<ContentDto>
    
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Query("ref") ref: String? = null
    ): ContentDto
    
    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("ref") ref: String? = null
    ): ContentDto
    
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<IssueDto>
    
    @GET("repos/{owner}/{repo}/issues/{number}")
    suspend fun getIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): IssueDto
    
    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<PullRequestDto>
    
    @GET("repos/{owner}/{repo}/pulls/{number}")
    suspend fun getPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): PullRequestDto
    
    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") sha: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<CommitDto>
    
    @GET("repos/{owner}/{repo}/commits/{sha}")
    suspend fun getCommit(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("sha") sha: String
    ): CommitDetailDto
    
    @GET("users/{username}/starred")
    suspend fun getStarredRepos(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<RepositoryDto>
    
    @PUT("user/starred/{owner}/{repo}")
    suspend fun starRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    )
    
    @DELETE("user/starred/{owner}/{repo}")
    suspend fun unstarRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    )
    
    @GET("users/{username}/followers")
    suspend fun getFollowers(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<UserBriefDto>
    
    @GET("users/{username}/following")
    suspend fun getFollowing(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<UserBriefDto>
    
    @GET("users/{username}/events")
    suspend fun getUserEvents(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<EventDto>
    
    @GET("issues")
    suspend fun getCurrentUserIssues(
        @Query("filter") filter: String = "all",
        @Query("state") state: String = "all",
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<IssueDto>
    
    @GET("user/orgs")
    suspend fun getCurrentUserOrgs(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<UserBriefDto>
    
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "best-match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResult<RepositoryDto>
    
    @GET("search/issues")
    suspend fun searchIssues(
        @Query("q") query: String,
        @Query("sort") sort: String = "best-match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResult<IssueDto>
    
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("sort") sort: String = "best-match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResult<UserBriefDto>
    
    @GET("notifications")
    suspend fun getNotifications(
        @Query("all") all: Boolean = false,
        @Query("participating") participating: Boolean = false,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): List<NotificationDto>
    
    @PUT("notifications")
    suspend fun markNotificationsAsRead(
        @Body body: MarkReadRequest? = null
    )
    
    @GET("notifications/threads/{thread_id}")
    suspend fun getNotificationThread(@Path("thread_id") threadId: Int): NotificationDto
    
    @PATCH("notifications/threads/{thread_id}")
    suspend fun markThreadAsRead(@Path("thread_id") threadId: Int)
    
    @DELETE("notifications/threads/{thread_id}")
    suspend fun markThreadAsDone(@Path("thread_id") threadId: Int)
    
    @GET("repos/{owner}/{repo}/actions/workflows")
    suspend fun getWorkflows(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): WorkflowListDto
    
    @GET("repos/{owner}/{repo}/actions/workflows/{workflow_id}/runs")
    suspend fun getWorkflowRuns(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("workflow_id") workflowId: String,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): WorkflowRunListDto
    
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("anon") anon: String? = null,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): List<ContributorDto>
    
    @GET("repos/{owner}/{repo}/languages")
    suspend fun getLanguages(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Map<String, Int>
}

interface GitHubAuthApi {
    @Headers("Accept: application/json")
    @POST("device/code")
    @FormUrlEncoded
    suspend fun getDeviceCode(
        @Field("client_id") clientId: String,
        @Field("scope") scope: String
    ): DeviceCodeDto
    
    @Headers("Accept: application/json")
    @POST("oauth/access_token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("device_code") deviceCode: String,
        @Field("grant_type") grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
    ): AccessTokenDto
}

data class SearchResult<T>(
    val totalCount: Int,
    val incompleteResults: Boolean = false,
    val items: List<T>
)