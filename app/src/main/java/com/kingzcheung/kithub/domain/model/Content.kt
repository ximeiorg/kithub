package com.kingzcheung.kithub.domain.model

data class Content(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String? = null,
    val htmlUrl: String? = null,
    val gitUrl: String? = null,
    val downloadUrl: String? = null,
    val type: ContentType,
    val content: String? = null,
    val encoding: String? = null
)

enum class ContentType {
    FILE, DIR, SYMLINK, SUBMODULE;
    
    fun toApiValue(): String = when (this) {
        FILE -> "file"
        DIR -> "dir"
        SYMLINK -> "symlink"
        SUBMODULE -> "submodule"
    }
    
    companion object {
        fun fromApiValue(value: String): ContentType = when (value.lowercase()) {
            "file" -> FILE
            "dir" -> DIR
            "symlink" -> SYMLINK
            "submodule" -> SUBMODULE
            else -> FILE
        }
    }
}

data class Branch(
    val name: String,
    val commit: BranchCommit,
    val protected: Boolean = false
)

data class BranchCommit(
    val sha: String,
    val url: String? = null
)

data class Readme(
    val name: String,
    val path: String,
    val sha: String,
    val content: String? = null,
    val encoding: String? = null,
    val htmlUrl: String? = null,
    val downloadUrl: String? = null
)