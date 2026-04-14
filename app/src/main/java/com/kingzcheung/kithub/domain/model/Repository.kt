package com.kingzcheung.kithub.domain.model

data class Repository(
    val id: Long,
    val nodeId: String? = null,
    val name: String,
    val fullName: String,
    val owner: UserBrief,
    val private: Boolean = false,
    val htmlUrl: String,
    val description: String? = null,
    val fork: Boolean = false,
    val url: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val pushedAt: String? = null,
    val homepage: String? = null,
    val size: Int = 0,
    val stargazersCount: Int = 0,
    val watchersCount: Int = 0,
    val language: String? = null,
    val hasIssues: Boolean = true,
    val hasProjects: Boolean = true,
    val hasDownloads: Boolean = true,
    val hasWiki: Boolean = true,
    val hasPages: Boolean = false,
    val forksCount: Int = 0,
    val archived: Boolean = false,
    val disabled: Boolean = false,
    val openIssuesCount: Int = 0,
    val license: License? = null,
    val forks: Int = 0,
    val openIssues: Int = 0,
    val watchers: Int = 0,
    val defaultBranch: String = "main",
    val permissions: RepoPermissions? = null
)

data class RepoPermissions(
    val admin: Boolean = false,
    val push: Boolean = false,
    val pull: Boolean = true
)

data class License(
    val key: String,
    val name: String,
    val spdxId: String? = null,
    val url: String? = null
)