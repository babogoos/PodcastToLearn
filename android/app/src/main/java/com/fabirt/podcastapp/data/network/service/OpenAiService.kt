package com.fabirt.podcastapp.data.network.service

import com.fabirt.podcastapp.data.network.constant.OpenAiApi
import com.fabirt.podcastapp.data.network.model.chat.ChatCompletionRequest
import com.fabirt.podcastapp.data.network.model.chat.ChatCompletionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OpenAiService {
    @Multipart
    @POST(OpenAiApi.AUDIO_TRANSCRIPTIONS)
    suspend fun audioTranscriptions(
        @Header("Authorization") authorization: String = OpenAiApi.AUTHENTICATION,
        @Part("model") model: RequestBody = OpenAiApi.AUDIO_TRANSCRIPTIONS_MODEL,
        @Part("response_format") responseFormat: RequestBody = OpenAiApi.TRANSCRIPTIONS_FORMAT,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST(OpenAiApi.CHAT_COMPLETIONS)
    suspend fun chatCompletions(
        @Body request: ChatCompletionRequest,
        @Header("Authorization") authorization: String = OpenAiApi.AUTHENTICATION
    ): Response<ChatCompletionResponse>
}