package com.example.frindshipassignment.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor (private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(authenticatedRequest)
    }
}