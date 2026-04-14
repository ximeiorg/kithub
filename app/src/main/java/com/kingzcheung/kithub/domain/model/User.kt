package com.kingzcheung.kithub.domain.model

data class User(
    val id: Long,
    val login: String,
    val nodeId: String? = null,
    val avatarUrl: String,
    val gravatarId: String? = null,
    val htmlUrl: String,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val hireable: Boolean? = null,
    val bio: String? = null,
    val twitterUsername: String? = null,
    val publicRepos: Int = 0,
    val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val type: String = "User",
    val siteAdmin: Boolean = false
)

data class UserBrief(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String = "User",
    val siteAdmin: Boolean = false
)