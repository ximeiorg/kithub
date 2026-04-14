package com.kingzcheung.kithub.domain.model

data class Commit(
    val sha: String,
    val nodeId: String? = null,
    val url: String? = null,
    val htmlUrl: String? = null,
    val author: CommitAuthor,
    val committer: CommitAuthor,
    val message: String,
    val tree: CommitTree? = null,
    val parents: List<CommitParent> = emptyList(),
    val stats: CommitStats? = null,
    val files: List<CommitFile>? = null
)

data class CommitBrief(
    val sha: String,
    val author: UserBrief? = null,
    val committer: UserBrief? = null,
    val message: String? = null,
    val htmlUrl: String? = null
)

data class CommitAuthor(
    val name: String,
    val email: String,
    val date: String,
    val avatarUrl: String? = null
)

data class CommitTree(
    val sha: String,
    val url: String? = null
)

data class CommitParent(
    val sha: String,
    val url: String? = null,
    val htmlUrl: String? = null
)

data class CommitStats(
    val additions: Int,
    val deletions: Int,
    val total: Int
)

data class CommitFile(
    val sha: String? = null,
    val filename: String,
    val status: String,
    val additions: Int,
    val deletions: Int,
    val changes: Int,
    val blobUrl: String? = null,
    val rawUrl: String? = null,
    val contentsUrl: String? = null,
    val patch: String? = null
)