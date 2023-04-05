package com.fabirt.podcastapp.data.network.client

import android.content.Context
import com.fabirt.podcastapp.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import tw.ktrssreader.Reader
import tw.ktrssreader.kotlin.model.channel.ITunesChannel
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by dion on 2023/04/05.
 */
object RSSReaderClient {
    suspend fun downloadFile(@ApplicationContext context: Context, url: String, fileName: String): File? =
        withContext(Dispatchers.IO) {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder().url(url).build()
            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val file = File(context.cacheDir, fileName)
                    val inputStream = response.body?.byteStream()
                    val outputStream = file.outputStream()

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    file
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

    suspend fun fecthRssPodcast(rssSource: String): ITunesChannel = Reader.coRead(rssSource)

    suspend fun postAudioTranscription(token: String, file: File, model: String): String? = withContext(Dispatchers.IO) {
        val url = "https://api.openai.com/v1/audio/transcriptions"
        val client = OkHttpClient.Builder()
            .callTimeout(600, TimeUnit.SECONDS)
            .build()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("model", model)
            .addFormDataPart("response_format", "srt")
            .addFormDataPart("file", file.name, file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            return@withContext response.body?.string()
        } else {
            return@withContext null
        }
    }

}