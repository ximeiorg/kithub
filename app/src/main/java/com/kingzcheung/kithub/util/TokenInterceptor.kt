package com.kingzcheung.kithub.util

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    
    companion object {
        private const val TAG = "TokenInterceptor"
        private val TOKEN_KEY = stringPreferencesKey("access_token")
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStore.data.map { preferences ->
                preferences[TOKEN_KEY]
            }.first()
        }
        
        val request = chain.request()
        Log.d(TAG, "Request: ${request.method} ${request.url}")
        
        val newRequest = if (token != null) {
            Log.d(TAG, "Using token: ${token.take(10)}...")
            request.newBuilder()
                .header("Authorization", "token $token")
                .build()
        } else {
            Log.w(TAG, "No token available for request: ${request.url}")
            request
        }
        
        val response = chain.proceed(newRequest)
        Log.d(TAG, "Response: ${response.code} for ${request.url}")
        
        return response
    }
}