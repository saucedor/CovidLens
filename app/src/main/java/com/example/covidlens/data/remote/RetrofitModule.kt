package com.example.covidlens.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModule {

    // You should move this to a more secure place like BuildConfig
    private const val BASE_URL = "https://covid-19-data.p.rapidapi.com/"

    fun create(apiKey: String): CovidApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CovidApi::class.java)
    }
}
