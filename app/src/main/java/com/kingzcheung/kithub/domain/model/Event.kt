package com.kingzcheung.kithub.domain.model

data class Event(
    val id: String,
    val type: EventType,
    val actor: UserBrief? = null,
    val repo: EventRepo? = null,
    val org: UserBrief? = null,
    val createdAt: String,
    val payload: Map<String, Any?>? = null
)

enum class EventType {
    CommitCommentEvent,
    CreateEvent,
    DeleteEvent,
    ForkEvent,
    GollumEvent,
    IssueCommentEvent,
    IssuesEvent,
    MemberEvent,
    PublicEvent,
    PullRequestEvent,
    PullRequestReviewEvent,
    PullRequestReviewCommentEvent,
    PushEvent,
    ReleaseEvent,
    SponsorshipEvent,
    WatchEvent,
    WorkflowRunEvent,
    WorkflowDispatchEvent;
    
    companion object {
        fun fromApiValue(value: String): EventType = when (value) {
            "CommitCommentEvent" -> CommitCommentEvent
            "CreateEvent" -> CreateEvent
            "DeleteEvent" -> DeleteEvent
            "ForkEvent" -> ForkEvent
            "GollumEvent" -> GollumEvent
            "IssueCommentEvent" -> IssueCommentEvent
            "IssuesEvent" -> IssuesEvent
            "MemberEvent" -> MemberEvent
            "PublicEvent" -> PublicEvent
            "PullRequestEvent" -> PullRequestEvent
            "PullRequestReviewEvent" -> PullRequestReviewEvent
            "PullRequestReviewCommentEvent" -> PullRequestReviewCommentEvent
            "PushEvent" -> PushEvent
            "ReleaseEvent" -> ReleaseEvent
            "SponsorshipEvent" -> SponsorshipEvent
            "WatchEvent" -> WatchEvent
            "WorkflowRunEvent" -> WorkflowRunEvent
            "WorkflowDispatchEvent" -> WorkflowDispatchEvent
            else -> WatchEvent
        }
    }
}

data class EventRepo(
    val id: Int,
    val name: String,
    val url: String? = null
)