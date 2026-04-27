package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.Release
import com.kingzcheung.kithub.domain.model.ReleaseAsset

data class ReleaseDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String? = null,
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("target_commitish") val targetCommitish: String,
    val name: String? = null,
    val body: String? = null,
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("published_at") val publishedAt: String? = null,
    val author: UserBriefDto,
    @SerializedName("tarball_url") val tarballUrl: String? = null,
    @SerializedName("zipball_url") val zipballUrl: String? = null,
    @SerializedName("html_url") val htmlUrl: String,
    val assets: List<ReleaseAssetDto> = emptyList()
) {
    fun toDomain(): Release = Release(
        id = id,
        nodeId = nodeId,
        tagName = tagName,
        targetCommitish = targetCommitish,
        name = name,
        body = body,
        draft = draft,
        prerelease = prerelease,
        createdAt = createdAt,
        publishedAt = publishedAt,
        author = author.toDomain(),
        tarballUrl = tarballUrl,
        zipballUrl = zipballUrl,
        htmlUrl = htmlUrl,
        assets = assets.map { it.toDomain() }
    )
}

data class ReleaseAssetDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String? = null,
    val name: String,
    val label: String? = null,
    val state: String,
    @SerializedName("content_type") val contentType: String? = null,
    val size: Int,
    @SerializedName("download_count") val downloadCount: Int = 0,
    @SerializedName("browser_download_url") val browserDownloadUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
) {
    fun toDomain(): ReleaseAsset = ReleaseAsset(
        id = id,
        nodeId = nodeId,
        name = name,
        label = label,
        state = state,
        contentType = contentType,
        size = size,
        downloadCount = downloadCount,
        browserDownloadUrl = browserDownloadUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}