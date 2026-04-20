package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.Workflow
import com.kingzcheung.kithub.domain.model.WorkflowList
import com.kingzcheung.kithub.domain.model.WorkflowState

data class WorkflowDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String,
    val name: String,
    val path: String,
    val state: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val url: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("badge_url") val badgeUrl: String
) {
    fun toDomain(): Workflow = Workflow(
        id = id,
        nodeId = nodeId,
        name = name,
        path = path,
        state = WorkflowState.fromApiValue(state),
        createdAt = createdAt,
        updatedAt = updatedAt,
        url = url,
        htmlUrl = htmlUrl,
        badgeUrl = badgeUrl
    )
}

data class WorkflowListDto(
    @SerializedName("total_count") val totalCount: Int,
    val workflows: List<WorkflowDto>
) {
    fun toDomain(): WorkflowList = WorkflowList(
        totalCount = totalCount,
        workflows = workflows.map { it.toDomain() }
    )
}