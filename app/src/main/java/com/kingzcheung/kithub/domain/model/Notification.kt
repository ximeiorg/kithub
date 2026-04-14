package com.kingzcheung.kithub.domain.model

data class NotificationThread(
    val id: String,
    val repository: Repository,
    val subject: NotificationSubject,
    val reason: NotificationReason,
    val unread: Boolean = false,
    val updatedAt: String,
    val lastReadAt: String? = null,
    val url: String,
    val subscriptionUrl: String? = null
)

data class NotificationSubject(
    val title: String,
    val url: String? = null,
    val latestCommentUrl: String? = null,
    val type: String
)

enum class NotificationReason {
    ApprovalRequested,
    Assign,
    Author,
    CiActivity,
    Comment,
    Invitation,
    Manual,
    MemberFeatureRequested,
    Mention,
    ReviewRequested,
    SecurityAdvisoryCredit,
    SecurityAlert,
    StateChange,
    Subscribed,
    TeamMention;
    
    companion object {
        fun fromApiValue(value: String): NotificationReason = when (value) {
            "approval_requested" -> ApprovalRequested
            "assign" -> Assign
            "author" -> Author
            "ci_activity" -> CiActivity
            "comment" -> Comment
            "invitation" -> Invitation
            "manual" -> Manual
            "member_feature_requested" -> MemberFeatureRequested
            "mention" -> Mention
            "review_requested" -> ReviewRequested
            "security_advisory_credit" -> SecurityAdvisoryCredit
            "security_alert" -> SecurityAlert
            "state_change" -> StateChange
            "subscribed" -> Subscribed
            "team_mention" -> TeamMention
            else -> Subscribed
        }
    }
    
    fun displayText(): String = when (this) {
        ApprovalRequested -> "Approval requested"
        Assign -> "Assigned"
        Author -> "Author"
        CiActivity -> "CI activity"
        Comment -> "Commented"
        Invitation -> "Invitation"
        Manual -> "Manually subscribed"
        MemberFeatureRequested -> "Feature requested"
        Mention -> "Mentioned"
        ReviewRequested -> "Review requested"
        SecurityAdvisoryCredit -> "Security advisory credit"
        SecurityAlert -> "Security alert"
        StateChange -> "State changed"
        Subscribed -> "Subscribed"
        TeamMention -> "Team mentioned"
    }
}