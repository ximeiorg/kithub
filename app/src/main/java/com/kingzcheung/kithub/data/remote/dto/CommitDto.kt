package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.*

data class CommitDto(
    val sha: String,
    @SerializedName("node_id") val nodeId: String? = null,
    val url: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    val author: UserBriefDto? = null,
    val committer: UserBriefDto? = null,
    val commit: CommitDetailInfoDto
) {
    fun toDomain(): CommitBrief = CommitBrief(
        sha = sha,
        author = author?.toDomain(),
        committer = committer?.toDomain(),
        message = commit.message ?: "",
        htmlUrl = htmlUrl
    )
}

data class CommitDetailDto(
    val sha: String,
    @SerializedName("node_id") val nodeId: String? = null,
    val url: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    val author: UserBriefDto? = null,
    val committer: UserBriefDto? = null,
    val commit: CommitDetailInfoDto,
    val files: List<CommitFileDto>? = null,
    val stats: CommitStatsDto? = null
) {
    fun toDomain(): Commit = Commit(
        sha = sha,
        nodeId = nodeId,
        url = url,
        htmlUrl = htmlUrl,
        author = commit.toDomainAuthor() ?: CommitAuthor(name = "Unknown", email = "", date = ""),
        committer = commit.toDomainCommitter() ?: CommitAuthor(name = "Unknown", email = "", date = ""),
        message = commit.message ?: "",
        stats = stats?.toDomain(),
        files = files?.map { it.toDomain() }
    )
}

data class CommitDetailInfoDto(
    val author: CommitAuthorDto? = null,
    val committer: CommitAuthorDto? = null,
    val message: String? = null,
    val tree: CommitTreeDto? = null
) {
    fun toDomainAuthor(): CommitAuthor? = author?.toDomain()
    fun toDomainCommitter(): CommitAuthor? = committer?.toDomain()
}

data class CommitAuthorDto(
    val name: String? = null,
    val email: String? = null,
    val date: String? = null
) {
    fun toDomain(): CommitAuthor = CommitAuthor(
        name = name ?: "Unknown",
        email = email ?: "",
        date = date ?: ""
    )
}

data class CommitTreeDto(
    val sha: String,
    val url: String? = null
) {
    fun toDomain(): CommitTree = CommitTree(
        sha = sha,
        url = url
    )
}

data class CommitStatsDto(
    val additions: Int,
    val deletions: Int,
    val total: Int
) {
    fun toDomain(): CommitStats = CommitStats(
        additions = additions,
        deletions = deletions,
        total = total
    )
}

data class CommitFileDto(
    val sha: String? = null,
    val filename: String,
    val status: String,
    val additions: Int,
    val deletions: Int,
    val changes: Int,
    @SerializedName("blob_url") val blobUrl: String? = null,
    @SerializedName("raw_url") val rawUrl: String? = null,
    @SerializedName("contents_url") val contentsUrl: String? = null,
    val patch: String? = null
) {
    fun toDomain(): CommitFile = CommitFile(
        sha = sha,
        filename = filename,
        status = status,
        additions = additions,
        deletions = deletions,
        changes = changes,
        blobUrl = blobUrl,
        rawUrl = rawUrl,
        contentsUrl = contentsUrl,
        patch = patch
    )
}