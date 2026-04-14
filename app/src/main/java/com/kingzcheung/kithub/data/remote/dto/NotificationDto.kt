package com.kingzcheung.kithub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kingzcheung.kithub.domain.model.NotificationReason
import com.kingzcheung.kithub.domain.model.NotificationSubject
import com.kingzcheung.kithub.domain.model.NotificationThread

data class NotificationDto(
    val id: String,
    val repository: RepositoryDto,
    val subject: NotificationSubjectDto,
    val reason: String,
    val unread: Boolean = false,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("last_read_at") val lastReadAt: String? = null,
    val url: String,
    @SerializedName("subscription_url") val subscriptionUrl: String? = null
) {
    fun toDomain(): NotificationThread {
        return NotificationThread(
            id = id,
            repository = repository.toDomain(),
            subject = subject.toDomain(),
            reason = NotificationReason.fromApiValue(reason),
            unread = unread,
            updatedAt = updatedAt,
            lastReadAt = lastReadAt,
            url = url,
            subscriptionUrl = subscriptionUrl
        )
    }
}

data class NotificationSubjectDto(
    val title: String,
    val url: String? = null,
    @SerializedName("latest_comment_url") val latestCommentUrl: String? = null,
    val type: String
) {
    fun toDomain(): NotificationSubject {
        return NotificationSubject(
            title = title,
            url = url,
            latestCommentUrl = latestCommentUrl,
            type = type
        )
    }
}

data class MarkReadRequest(
    @SerializedName("last_read_at") val lastReadAt: String? = null,
    val read: Boolean = true
)