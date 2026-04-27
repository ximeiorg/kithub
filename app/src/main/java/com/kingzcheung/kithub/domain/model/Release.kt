package com.kingzcheung.kithub.domain.model

data class Release(
    val id: Long,
    val nodeId: String? = null,
    val tagName: String,
    val targetCommitish: String,
    val name: String? = null,
    val body: String? = null,
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    val createdAt: String,
    val publishedAt: String? = null,
    val author: UserBrief,
    val tarballUrl: String? = null,
    val zipballUrl: String? = null,
    val htmlUrl: String,
    val assets: List<ReleaseAsset> = emptyList()
)

data class ReleaseAsset(
    val id: Long,
    val nodeId: String? = null,
    val name: String,
    val label: String? = null,
    val state: String,
    val contentType: String? = null,
    val size: Int,
    val downloadCount: Int = 0,
    val browserDownloadUrl: String,
    val createdAt: String,
    val updatedAt: String
)