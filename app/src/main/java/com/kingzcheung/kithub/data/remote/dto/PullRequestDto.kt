package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.*

data class PullRequestDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String? = null,
    val number: Int,
    val title: String,
    val body: String? = null,
    @SerializedName("body_html") val bodyHtml: String? = null,
    val user: UserBriefDto? = null,
    val state: String,
    @SerializedName("html_url") val htmlUrl: String,
    val url: String,
    @SerializedName("diff_url") val diffUrl: String? = null,
    @SerializedName("patch_url") val patchUrl: String? = null,
    @SerializedName("issue_url") val issueUrl: String? = null,
    @SerializedName("commits_url") val commitsUrl: String? = null,
    @SerializedName("review_comments_url") val reviewCommentsUrl: String? = null,
    @SerializedName("comments_url") val commentsUrl: String? = null,
    @SerializedName("statuses_url") val statusesUrl: String? = null,
    val draft: Boolean = false,
    val merged: Boolean = false,
    val mergeable: Boolean? = null,
    val rebaseable: Boolean? = null,
    @SerializedName("mergeable_state") val mergeableState: String? = null,
    @SerializedName("merged_at") val mergedAt: String? = null,
    @SerializedName("merged_by") val mergedBy: UserBriefDto? = null,
    @SerializedName("merge_commit_sha") val mergeCommitSha: String? = null,
    val head: PullRequestBranchDto? = null,
    val base: PullRequestBranchDto? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("closed_at") val closedAt: String? = null,
    val assignee: UserBriefDto? = null,
    val assignees: List<UserBriefDto> = emptyList(),
    @SerializedName("requested_reviewers") val requestedReviewers: List<UserBriefDto> = emptyList(),
    @SerializedName("requested_teams") val requestedTeams: List<TeamBriefDto> = emptyList(),
    val labels: List<IssueLabelDto> = emptyList(),
    val milestone: MilestoneDto? = null,
    @SerializedName("active_lock_reason") val activeLockReason: String? = null,
    val locked: Boolean = false,
    @SerializedName("author_association") val authorAssociation: String? = null,
    @SerializedName("auto_merge") val autoMerge: AutoMergeDto? = null,
    @SerializedName("maintainer_can_modify") val maintainerCanModify: Boolean? = null,
    val commits: Int = 0,
    val additions: Int = 0,
    val deletions: Int = 0,
    @SerializedName("changed_files") val changedFiles: Int = 0,
    val comments: Int = 0,
    @SerializedName("review_comments") val reviewComments: Int = 0,
    @SerializedName("_links") val links: PullRequestLinksDto? = null
) {
    fun toDomain(): PullRequest = PullRequest(
        id = id,
        nodeId = nodeId,
        number = number,
        title = title,
        body = body ?: "",
        bodyHtml = bodyHtml,
        user = user?.toDomain() ?: UserBrief(id = 0, login = "unknown", avatarUrl = "", htmlUrl = ""),
        state = IssueState.fromApiValue(state),
        htmlUrl = htmlUrl,
        url = url,
        diffUrl = diffUrl,
        patchUrl = patchUrl,
        issueUrl = issueUrl,
        commitsUrl = commitsUrl,
        reviewCommentsUrl = reviewCommentsUrl,
        commentsUrl = commentsUrl,
        statusesUrl = statusesUrl,
        draft = draft,
        merged = merged,
        mergeable = mergeable,
        rebaseable = rebaseable,
        mergeableState = mergeableState,
        mergedAt = mergedAt,
        mergedBy = mergedBy?.toDomain(),
        mergeCommitSha = mergeCommitSha,
        head = head?.toDomain() ?: PullRequestBranch(label = "", ref = "", sha = ""),
        base = base?.toDomain() ?: PullRequestBranch(label = "", ref = "", sha = ""),
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        closedAt = closedAt,
        assignee = assignee?.toDomain(),
        assignees = assignees.map { it.toDomain() },
        requestedReviewers = requestedReviewers.map { it.toDomain() },
        requestedTeams = requestedTeams.map { it.toDomain() },
        labels = labels.map { it.toDomain() },
        milestone = milestone?.toDomain(),
        activeLockReason = activeLockReason,
        locked = locked,
        authorAssociation = authorAssociation,
        autoMerge = autoMerge?.toDomain(),
        maintainerCanModify = maintainerCanModify,
        commits = commits,
        additions = additions,
        deletions = deletions,
        changedFiles = changedFiles,
        comments = comments,
        reviewComments = reviewComments,
        links = links?.toDomain()
    )
}

data class PullRequestBranchDto(
    val label: String? = null,
    val ref: String? = null,
    val sha: String? = null,
    val user: UserBriefDto? = null,
    val repo: RepositoryDto? = null
) {
    fun toDomain(): PullRequestBranch = PullRequestBranch(
        label = label ?: "",
        ref = ref ?: "",
        sha = sha ?: "",
        user = user?.toDomain(),
        repo = repo?.toDomain()
    )
}

data class TeamBriefDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String? = null,
    val url: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val description: String? = null,
    val privacy: String? = null
) {
    fun toDomain(): TeamBrief = TeamBrief(
        id = id,
        nodeId = nodeId,
        url = url,
        htmlUrl = htmlUrl,
        name = name ?: "",
        slug = slug ?: "",
        description = description,
        privacy = privacy
    )
}

data class AutoMergeDto(
    @SerializedName("enabled_by") val enabledBy: UserBriefDto? = null,
    @SerializedName("merge_method") val mergeMethod: String? = null,
    @SerializedName("commit_title") val commitTitle: String? = null,
    @SerializedName("commit_message") val commitMessage: String? = null
) {
    fun toDomain(): AutoMerge = AutoMerge(
        enabledBy = enabledBy?.toDomain(),
        mergeMethod = mergeMethod ?: "",
        commitTitle = commitTitle,
        commitMessage = commitMessage
    )
}

data class PullRequestLinksDto(
    val self: LinkDto? = null,
    val html: LinkDto? = null,
    val issue: LinkDto? = null,
    val comments: LinkDto? = null,
    @SerializedName("review_comments") val reviewComments: LinkDto? = null,
    val commits: LinkDto? = null,
    val statuses: LinkDto? = null
) {
    fun toDomain(): PullRequestLinks = PullRequestLinks(
        self = self?.toDomain(),
        html = html?.toDomain(),
        issue = issue?.toDomain(),
        comments = comments?.toDomain(),
        reviewComments = reviewComments?.toDomain(),
        commits = commits?.toDomain(),
        statuses = statuses?.toDomain()
    )
}

data class LinkDto(
    val href: String? = null
) {
    fun toDomain(): Link = Link(href = href ?: "")
}