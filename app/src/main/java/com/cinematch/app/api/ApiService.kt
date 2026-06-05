package com.cinematch.app.api

import com.cinematch.app.model.RecommendRequest
import com.cinematch.app.model.RecommendResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("recommend")
    suspend fun recommend(@Body request: RecommendRequest): Response<RecommendResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://moviemoodmatcher-production.up.railway.app/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}