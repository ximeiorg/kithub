package com.kingzcheung.kithub.domain.model

data class WorkflowRun(
    val id: Long,
    val name: String,
    val nodeId: String,
    val headBranch: String,
    val headSha: String,
    val path: String,
    val runNumber: Int,
    val event: String,
    val displayTitle: String,
    val status: WorkflowRunStatus,
    val conclusion: WorkflowRunConclusion?,
    val workflowId: Long,
    val url: String,
    val htmlUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val runAttempt: Int,
    val runStartedAt: String,
    val triggeringActor: UserBrief?,
    val jobsUrl: String,
    val logsUrl: String
)

enum class WorkflowRunStatus {
    QUEUED,
    IN_PROGRESS,
    COMPLETED,
    WAITING,
    PENDING,
    REQUESTED;
    
    companion object {
        fun fromApiValue(value: String): WorkflowRunStatus = when (value.lowercase()) {
            "queued" -> QUEUED
            "in_progress" -> IN_PROGRESS
            "completed" -> COMPLETED
            "waiting" -> WAITING
            "pending" -> PENDING
            "requested" -> REQUESTED
            else -> QUEUED
        }
    }
}

enum class WorkflowRunConclusion {
    SUCCESS,
    FAILURE,
    CANCELLED,
    TIMED_OUT,
    SKIPPED,
    ACTION_REQUIRED,
    NEUTRAL;
    
    companion object {
        fun fromApiValue(value: String?): WorkflowRunConclusion? = when (value?.lowercase()) {
            "success" -> SUCCESS
            "failure" -> FAILURE
            "cancelled" -> CANCELLED
            "timed_out" -> TIMED_OUT
            "skipped" -> SKIPPED
            "action_required" -> ACTION_REQUIRED
            "neutral" -> NEUTRAL
            null -> null
            else -> null
        }
    }
}

data class WorkflowRunList(
    val totalCount: Int,
    val workflowRuns: List<WorkflowRun>
)