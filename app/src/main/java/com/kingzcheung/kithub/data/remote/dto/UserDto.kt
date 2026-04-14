package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.*

data class UserDto(
    val id: Long? = null,
    val login: String? = null,
    @SerializedName("node_id") val nodeId: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("gravatar_id") val gravatarId: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val hireable: Boolean? = null,
    val bio: String? = null,
    @SerializedName("twitter_username") val twitterUsername: String? = null,
    @SerializedName("public_repos") val publicRepos: Int = 0,
    @SerializedName("public_gists") val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val type: String = "User",
    @SerializedName("site_admin") val siteAdmin: Boolean = false
) {
    fun toDomain(): User = User(
        id = id ?: 0,
        login = login ?: "unknown",
        nodeId = nodeId,
        avatarUrl = avatarUrl ?: "",
        gravatarId = gravatarId,
        htmlUrl = htmlUrl ?: "",
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        hireable = hireable,
        bio = bio,
        twitterUsername = twitterUsername,
        publicRepos = publicRepos,
        publicGists = publicGists,
        followers = followers,
        following = following,
        createdAt = createdAt,
        updatedAt = updatedAt,
        type = type,
        siteAdmin = siteAdmin
    )
}

data class UserBriefDto(
    val id: Long? = null,
    val login: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    val type: String? = null,
    @SerializedName("site_admin") val siteAdmin: Boolean? = null
) {
    fun toDomain(): UserBrief {
        return UserBrief(
            id = id ?: 0,
            login = login ?: "unknown",
            avatarUrl = avatarUrl ?: "",
            htmlUrl = htmlUrl ?: "https://github.com/${login ?: "unknown"}",
            type = type ?: "User",
            siteAdmin = siteAdmin ?: false
        )
    }
}