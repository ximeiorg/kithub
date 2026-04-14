package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.*

data class ContentDto(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String? = null,
    @SerializedName("html_url") val htmlUrl: String? = null,
    @SerializedName("git_url") val gitUrl: String? = null,
    @SerializedName("download_url") val downloadUrl: String? = null,
    val type: String,
    val content: String? = null,
    val encoding: String? = null
) {
    fun toDomain(): Content = Content(
        name = name,
        path = path,
        sha = sha,
        size = size,
        url = url,
        htmlUrl = htmlUrl,
        gitUrl = gitUrl,
        downloadUrl = downloadUrl,
        type = ContentType.fromApiValue(type),
        content = content,
        encoding = encoding
    )
}

data class BranchDto(
    val name: String,
    val commit: BranchCommitDto,
    val protected: Boolean = false
) {
    fun toDomain(): Branch = Branch(
        name = name,
        commit = commit.toDomain(),
        protected = protected
    )
}

data class BranchCommitDto(
    val sha: String,
    val url: String? = null
) {
    fun toDomain(): BranchCommit = BranchCommit(
        sha = sha,
        url = url
    )
}

data class EventActorDto(
    val id: Long? = null,
    val login: String? = null,
    @SerializedName("display_login") val displayLogin: String? = null,
    @SerializedName("gravatar_id") val gravatarId: String? = null,
    val url: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
) {
    fun toDomain(): UserBrief? {
        if (id == null || login == null || avatarUrl == null) return null
        return UserBrief(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            htmlUrl = "https://github.com/$login",
            type = "User",
            siteAdmin = false
        )
    }
}

data class EventDto(
    val id: String,
    val type: String,
    val actor: EventActorDto? = null,
    val repo: EventRepoDto? = null,
    val org: EventActorDto? = null,
    @SerializedName("created_at") val createdAt: String,
    val payload: Map<String, Any?>? = null
) {
    fun toDomain(): Event = Event(
        id = id,
        type = EventType.fromApiValue(type),
        actor = actor?.toDomain(),
        repo = repo?.toDomain(),
        org = org?.toDomain(),
        createdAt = createdAt,
        payload = payload
    )
}

data class EventRepoDto(
    val id: Int,
    val name: String,
    val url: String? = null
) {
    fun toDomain(): EventRepo = EventRepo(
        id = id,
        name = name,
        url = url
    )
}

data class DeviceCodeDto(
    @SerializedName("device_code") val deviceCode: String,
    @SerializedName("user_code") val userCode: String,
    @SerializedName("verification_uri") val verificationUri: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("interval") val interval: Int
) {
    fun toDomain(): DeviceCode = DeviceCode(
        deviceCode = deviceCode,
        userCode = userCode,
        verificationUri = verificationUri,
        expiresIn = expiresIn,
        interval = interval
    )
}

data class AccessTokenDto(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("token_type") val tokenType: String = "bearer",
    val scope: String? = null,
    val error: String? = null,
    @SerializedName("error_description") val errorDescription: String? = null,
    @SerializedName("error_uri") val errorUri: String? = null
) {
    fun toDomain(): AccessToken = AccessToken(
        accessToken = accessToken ?: "",
        tokenType = tokenType,
        scope = scope,
        error = error,
        errorDescription = errorDescription,
        errorUri = errorUri
    )
}