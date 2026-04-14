package com.kingzcheung.kithub.domain.model

data class PullRequest(
    val id: Long,
    val nodeId: String? = null,
    val number: Int,
    val title: String,
    val body: String? = null,
    val bodyHtml: String? = null,
    val user: UserBrief,
    val state: IssueState,
    val htmlUrl: String,
    val url: String,
    val diffUrl: String? = null,
    val patchUrl: String? = null,
    val issueUrl: String? = null,
    val repositoryUrl: String? = null,
    val commitsUrl: String? = null,
    val reviewCommentsUrl: String? = null,
    val commentsUrl: String? = null,
    val statusesUrl: String? = null,
    val draft: Boolean = false,
    val merged: Boolean = false,
    val mergeable: Boolean? = null,
    val rebaseable: Boolean? = null,
    val mergeableState: String? = null,
    val mergedAt: String? = null,
    val mergedBy: UserBrief? = null,
    val mergeCommitSha: String? = null,
    val head: PullRequestBranch,
    val base: PullRequestBranch,
    val createdAt: String,
    val updatedAt: String,
    val closedAt: String? = null,
    val assignee: UserBrief? = null,
    val assignees: List<UserBrief> = emptyList(),
    val requestedReviewers: List<UserBrief> = emptyList(),
    val requestedTeams: List<TeamBrief> = emptyList(),
    val labels: List<IssueLabel> = emptyList(),
    val milestone: Milestone? = null,
    val activeLockReason: String? = null,
    val locked: Boolean = false,
    val authorAssociation: String? = null,
    val autoMerge: AutoMerge? = null,
    val maintainerCanModify: Boolean? = null,
    val commits: Int = 0,
    val additions: Int = 0,
    val deletions: Int = 0,
    val changedFiles: Int = 0,
    val comments: Int = 0,
    val reviewComments: Int = 0,
    val links: PullRequestLinks? = null
)

data class PullRequestBranch(
    val label: String,
    val ref: String,
    val sha: String,
    val user: UserBrief? = null,
    val repo: Repository? = null
)

data class TeamBrief(
    val id: Long,
    val nodeId: String? = null,
    val url: String? = null,
    val htmlUrl: String? = null,
    val name: String,
    val slug: String,
    val description: String? = null,
    val privacy: String? = null
)

data class AutoMerge(
    val enabledBy: UserBrief? = null,
    val mergeMethod: String,
    val commitTitle: String? = null,
    val commitMessage: String? = null
)

data class PullRequestLinks(
    val self: Link? = null,
    val html: Link? = null,
    val issue: Link? = null,
    val comments: Link? = null,
    val reviewComments: Link? = null,
    val commits: Link? = null,
    val statuses: Link? = null
)

data class Link(
    val href: String
)