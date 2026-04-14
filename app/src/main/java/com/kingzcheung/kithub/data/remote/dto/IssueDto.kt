package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.*

data class IssueDto(
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
    @SerializedName("repository_url") val repositoryUrl: String? = null,
    @SerializedName("labels_url") val labelsUrl: String? = null,
    @SerializedName("comments_url") val commentsUrl: String? = null,
    @SerializedName("events_url") val eventsUrl: String? = null,
    val labels: List<IssueLabelDto> = emptyList(),
    val assignee: UserBriefDto? = null,
    val assignees: List<UserBriefDto> = emptyList(),
    val milestone: MilestoneDto? = null,
    val locked: Boolean = false,
    val comments: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("closed_at") val closedAt: String? = null,
    @SerializedName("closed_by") val closedBy: UserBriefDto? = null,
    @SerializedName("author_association") val authorAssociation: String? = null,
    @SerializedName("pull_request") val pullRequest: PullRequestRefDto? = null
) {
    fun toDomain(): Issue {
        val repoInfo = parseRepositoryUrl(repositoryUrl)
        return Issue(
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
            repositoryUrl = repositoryUrl,
            repositoryOwner = repoInfo.first,
            repositoryName = repoInfo.second,
            labelsUrl = labelsUrl,
            commentsUrl = commentsUrl,
            eventsUrl = eventsUrl,
            labels = labels.map { it.toDomain() },
            assignee = assignee?.toDomain(),
            assignees = assignees.map { it.toDomain() },
            milestone = milestone?.toDomain(),
            locked = locked,
            comments = comments,
            createdAt = createdAt ?: "",
            updatedAt = updatedAt ?: "",
            closedAt = closedAt,
            closedBy = closedBy?.toDomain(),
            authorAssociation = authorAssociation,
            pullRequest = pullRequest?.toDomain()
        )
    }
    
    private fun parseRepositoryUrl(url: String?): Pair<String, String> {
        if (url == null) return Pair("", "")
        val parts = url.removePrefix("https://api.github.com/repos/").split("/")
        return if (parts.size >= 2) {
            Pair(parts[0], parts[1])
        } else {
            Pair("", "")
        }
    }
}

data class IssueLabelDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String? = null,
    val url: String? = null,
    val name: String,
    val color: String,
    val default: Boolean = false,
    val description: String? = null
) {
    fun toDomain(): IssueLabel = IssueLabel(
        id = id,
        nodeId = nodeId,
        url = url,
        name = name,
        color = color,
        default = default,
        description = description
    )
}

data class MilestoneDto(
    val id: Long,
    val number: Int,
    val title: String,
    val description: String? = null,
    val state: String = "open",
    @SerializedName("open_issues") val openIssues: Int = 0,
    @SerializedName("closed_issues") val closedIssues: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("due_on") val dueOn: String? = null,
    @SerializedName("closed_at") val closedAt: String? = null
) {
    fun toDomain(): Milestone = Milestone(
        id = id,
        number = number,
        title = title,
        description = description,
        state = state,
        openIssues = openIssues,
        closedIssues = closedIssues,
        createdAt = createdAt,
        updatedAt = updatedAt,
        dueOn = dueOn,
        closedAt = closedAt
    )
}

data class PullRequestRefDto(
    val url: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    @SerializedName("diff_url") val diffUrl: String? = null,
    @SerializedName("patch_url") val patchUrl: String? = null
) {
    fun toDomain(): PullRequestRef = PullRequestRef(
        url = url,
        htmlUrl = htmlUrl,
        diffUrl = diffUrl,
        patchUrl = patchUrl
    )
}