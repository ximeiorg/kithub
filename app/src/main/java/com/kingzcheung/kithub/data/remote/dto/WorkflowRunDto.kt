package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.WorkflowRun
import com.kingzcheung.kithub.domain.model.WorkflowRunConclusion
import com.kingzcheung.kithub.domain.model.WorkflowRunList
import com.kingzcheung.kithub.domain.model.WorkflowRunStatus

data class WorkflowRunDto(
    val id: Long,
    val name: String,
    @SerializedName("node_id") val nodeId: String,
    @SerializedName("head_branch") val headBranch: String,
    @SerializedName("head_sha") val headSha: String,
    val path: String,
    @SerializedName("run_number") val runNumber: Int,
    val event: String,
    @SerializedName("display_title") val displayTitle: String,
    val status: String,
    val conclusion: String?,
    @SerializedName("workflow_id") val workflowId: Long,
    val url: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("run_attempt") val runAttempt: Int,
    @SerializedName("run_started_at") val runStartedAt: String,
    @SerializedName("triggering_actor") val triggeringActor: UserBriefDto?,
    @SerializedName("jobs_url") val jobsUrl: String,
    @SerializedName("logs_url") val logsUrl: String
) {
    fun toDomain(): WorkflowRun = WorkflowRun(
        id = id,
        name = name,
        nodeId = nodeId,
        headBranch = headBranch,
        headSha = headSha,
        path = path,
        runNumber = runNumber,
        event = event,
        displayTitle = displayTitle,
        status = WorkflowRunStatus.fromApiValue(status),
        conclusion = WorkflowRunConclusion.fromApiValue(conclusion),
        workflowId = workflowId,
        url = url,
        htmlUrl = htmlUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        runAttempt = runAttempt,
        runStartedAt = runStartedAt,
        triggeringActor = triggeringActor?.toDomain(),
        jobsUrl = jobsUrl,
        logsUrl = logsUrl
    )
}

data class WorkflowRunListDto(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("workflow_runs") val workflowRuns: List<WorkflowRunDto>
) {
    fun toDomain(): WorkflowRunList = WorkflowRunList(
        totalCount = totalCount,
        workflowRuns = workflowRuns.map { it.toDomain() }
    )
}