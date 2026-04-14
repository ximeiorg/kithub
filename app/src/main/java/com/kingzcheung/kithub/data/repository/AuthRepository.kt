package com.kingzcheung.kithub.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kingzcheung.kithub.data.remote.api.GitHubAuthApi
import com.kingzcheung.kithub.domain.model.AccessToken
import com.kingzcheung.kithub.domain.model.DeviceCode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val authApi: GitHubAuthApi
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private const val CLIENT_ID = "Ov23liuMD8m7MI4A2EQd"
        private const val SCOPE = "repo,user"
    }
    
    val tokenFlow = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
    
    suspend fun getStoredToken(): String? = tokenFlow.first()
    
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
    
    suspend fun getDeviceCode(): DeviceCode {
        return authApi.getDeviceCode(CLIENT_ID, SCOPE).toDomain()
    }
    
    suspend fun getAccessToken(deviceCode: String): AccessToken {
        return authApi.getAccessToken(CLIENT_ID, deviceCode).toDomain()
    }
    
    suspend fun isAuthenticated(): Boolean = getStoredToken() != null
}