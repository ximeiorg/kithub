package com.kingzcheung.kithub.util

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        if (!response.isSuccessful) {
            val contentType = response.header("Content-Type")
            val bodyString = response.peekBody(Long.MAX_VALUE).string()
            
            if (contentType?.contains("text/html") == true || bodyString.trim().startsWith("<!DOCTYPE", ignoreCase = true) || bodyString.trim().startsWith("<html", ignoreCase = true)) {
                val errorMessage = when (response.code) {
                    403 -> "API rate limit exceeded or access forbidden. Please check your token."
                    401 -> "Unauthorized. Please login again."
                    404 -> "Resource not found: ${request.url}"
                    else -> "HTTP ${response.code}: ${request.url}"
                }
                response.close()
                throw IOException(errorMessage)
            }
        }
        
        return response
    }
}