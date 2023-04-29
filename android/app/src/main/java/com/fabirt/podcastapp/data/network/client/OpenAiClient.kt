package com.fabirt.podcastapp.data.network.client

import com.fabirt.podcastapp.data.network.constant.OpenAiApi
import com.fabirt.podcastapp.data.network.service.OpenAiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by dion on 2023/04/29.
 */
object OpenAiClient {
    fun createOpenAPIService(
        client: OkHttpClient
    ): OpenAiService {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(OpenAiApi.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .serializeNulls()
                        .serializeSpecialFloatingPointValues()
                        .setLenient()
                        .create()
                )
            )
            .build()
            .create(OpenAiService::class.java)
    }
}