package com.kingzcheung.kithub.domain.model

data class DeviceCode(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Int,
    val interval: Int
)

data class AccessToken(
    val accessToken: String,
    val tokenType: String = "bearer",
    val scope: String? = null,
    val error: String? = null,
    val errorDescription: String? = null,
    val errorUri: String? = null
)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val token: String? = null,
    val user: User? = null,
    val loading: Boolean = false,
    val error: String? = null
)