package com.dionchang.podcasttolearn.data.network.constant

import com.dionchang.podcasttolearn.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

object OpenAiApi {
    const val BASE_URL = "https://api.openai.com/v1/"
    const val AUDIO_TRANSCRIPTIONS = "audio/transcriptions"
    val AUDIO_TRANSCRIPTIONS_MODEL = "whisper-1".toRequestBody("multipart/form-data".toMediaTypeOrNull())
    val TRANSCRIPTIONS_FORMAT = "srt".toRequestBody("multipart/form-data".toMediaTypeOrNull())
    const val CHAT_COMPLETIONS = "chat/completions"
    const val AUTHENTICATION = "Bearer ${BuildConfig.OPEN_AI_TOKEN}"
}