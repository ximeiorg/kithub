package com.kingzcheung.kithub.domain.model

data class Issue(
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
    val repositoryUrl: String? = null,
    val repositoryOwner: String = "",
    val repositoryName: String = "",
    val labelsUrl: String? = null,
    val commentsUrl: String? = null,
    val eventsUrl: String? = null,
    val labels: List<IssueLabel> = emptyList(),
    val assignee: UserBrief? = null,
    val assignees: List<UserBrief> = emptyList(),
    val milestone: Milestone? = null,
    val locked: Boolean = false,
    val comments: Int = 0,
    val createdAt: String,
    val updatedAt: String,
    val closedAt: String? = null,
    val closedBy: UserBrief? = null,
    val authorAssociation: String? = null,
    val pullRequest: PullRequestRef? = null
)

enum class IssueState {
    OPEN, CLOSED, REOPENED;
    
    fun toApiValue(): String = when (this) {
        OPEN -> "open"
        CLOSED -> "closed"
        REOPENED -> "reopened"
    }
    
    companion object {
        fun fromApiValue(value: String): IssueState = when (value.lowercase()) {
            "open" -> OPEN
            "closed" -> CLOSED
            "reopened" -> REOPENED
            else -> OPEN
        }
    }
}

data class IssueLabel(
    val id: Long,
    val nodeId: String? = null,
    val url: String? = null,
    val name: String,
    val color: String,
    val default: Boolean = false,
    val description: String? = null
)

data class Milestone(
    val id: Long,
    val number: Int,
    val title: String,
    val description: String? = null,
    val state: String = "open",
    val openIssues: Int = 0,
    val closedIssues: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val dueOn: String? = null,
    val closedAt: String? = null
)

data class PullRequestRef(
    val url: String? = null,
    val htmlUrl: String? = null,
    val diffUrl: String? = null,
    val patchUrl: String? = null
)