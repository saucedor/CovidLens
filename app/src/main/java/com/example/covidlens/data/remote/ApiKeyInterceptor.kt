package com.example.covidlens.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val apiKeyProvider: ApiKeyProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", apiKeyProvider.getKey())
            .build()

        return chain.proceed(request)
    }
}