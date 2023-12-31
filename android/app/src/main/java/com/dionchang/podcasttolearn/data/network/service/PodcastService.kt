package com.dionchang.podcasttolearn.data.network.service

import com.dionchang.podcasttolearn.BuildConfig
import com.dionchang.podcasttolearn.data.network.constant.ListenNotesAPI
import com.dionchang.podcasttolearn.data.network.model.PodcastSearchDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface PodcastService {

    @GET(ListenNotesAPI.SEARCH)
    suspend fun searchPodcasts(
        @Query("q") query: String,
        @Query("type") type: String,
        @Header("X-ListenAPI-Key") apiKey: String = BuildConfig.API_KEY,
    ): PodcastSearchDto

    @Streaming
    @GET
    suspend fun downloadAudioFile(@Url url: String): Response<ResponseBody>
}