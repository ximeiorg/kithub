package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.*

data class RepositoryDto(
    val id: Long,
    @SerializedName("node_id") val nodeId: String? = null,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val owner: UserBriefDto? = null,
    val private: Boolean = false,
    @SerializedName("html_url") val htmlUrl: String,
    val description: String? = null,
    val fork: Boolean = false,
    val url: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("pushed_at") val pushedAt: String? = null,
    val homepage: String? = null,
    val size: Int = 0,
    @SerializedName("stargazers_count") val stargazersCount: Int = 0,
    @SerializedName("watchers_count") val watchersCount: Int = 0,
    val language: String? = null,
    @SerializedName("has_issues") val hasIssues: Boolean = true,
    @SerializedName("has_projects") val hasProjects: Boolean = true,
    @SerializedName("has_downloads") val hasDownloads: Boolean = true,
    @SerializedName("has_wiki") val hasWiki: Boolean = true,
    @SerializedName("has_pages") val hasPages: Boolean = false,
    @SerializedName("forks_count") val forksCount: Int = 0,
    val archived: Boolean = false,
    val disabled: Boolean = false,
    @SerializedName("open_issues_count") val openIssuesCount: Int = 0,
    val license: LicenseDto? = null,
    val forks: Int = 0,
    @SerializedName("open_issues") val openIssues: Int = 0,
    val watchers: Int = 0,
    @SerializedName("default_branch") val defaultBranch: String? = "main",
    val permissions: RepoPermissionsDto? = null
) {
    fun toDomain(): Repository = Repository(
        id = id,
        nodeId = nodeId,
        name = name,
        fullName = fullName,
        owner = owner?.toDomain() ?: UserBrief(id = 0, login = "unknown", avatarUrl = "", htmlUrl = ""),
        private = private,
        htmlUrl = htmlUrl,
        description = description,
        fork = fork,
        url = url,
        createdAt = createdAt,
        updatedAt = updatedAt,
        pushedAt = pushedAt,
        homepage = homepage,
        size = size,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        language = language,
        hasIssues = hasIssues,
        hasProjects = hasProjects,
        hasDownloads = hasDownloads,
        hasWiki = hasWiki,
        hasPages = hasPages,
        forksCount = forksCount,
        archived = archived,
        disabled = disabled,
        openIssuesCount = openIssuesCount,
        license = license?.toDomain(),
        forks = forks,
        openIssues = openIssues,
        watchers = watchers,
        defaultBranch = defaultBranch ?: "main",
        permissions = permissions?.toDomain()
    )
}

data class RepoPermissionsDto(
    val admin: Boolean = false,
    val push: Boolean = false,
    val pull: Boolean = true
) {
    fun toDomain(): RepoPermissions = RepoPermissions(
        admin = admin,
        push = push,
        pull = pull
    )
}

data class LicenseDto(
    val key: String,
    val name: String,
    @SerializedName("spdx_id") val spdxId: String? = null,
    val url: String? = null
) {
    fun toDomain(): License = License(
        key = key,
        name = name,
        spdxId = spdxId,
        url = url
    )
}