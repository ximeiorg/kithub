package com.kingzcheung.kithub.domain.model

data class Workflow(
    val id: Long,
    val nodeId: String,
    val name: String,
    val path: String,
    val state: WorkflowState,
    val createdAt: String,
    val updatedAt: String,
    val url: String,
    val htmlUrl: String,
    val badgeUrl: String
)

enum class WorkflowState {
    ACTIVE,
    DISABLED,
    DISABLED_MANUALLY;
    
    companion object {
        fun fromApiValue(value: String): WorkflowState = when (value.lowercase()) {
            "active" -> ACTIVE
            "disabled" -> DISABLED
            "disabled_manually" -> DISABLED_MANUALLY
            else -> ACTIVE
        }
    }
}

data class WorkflowList(
    val totalCount: Int,
    val workflows: List<Workflow>
)