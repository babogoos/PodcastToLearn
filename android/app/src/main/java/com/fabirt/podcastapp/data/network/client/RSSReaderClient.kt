package com.fabirt.podcastapp.data.network.client

import android.content.Context
import android.net.Uri
import androidx.core.text.HtmlCompat
import com.fabirt.podcastapp.BuildConfig
import com.fabirt.podcastapp.data.network.model.EpisodeDto
import com.fabirt.podcastapp.data.network.model.PodcastDto
import com.fabirt.podcastapp.data.network.model.PodcastSearchDto
import com.fabirt.podcastapp.ui.toDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
class RSSReaderClient(
    @ApplicationContext val context: Context,
) {

    // Download the podcast file and return the file
    fun downloadFile(url: String, fileName: String): File? {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .build()
        val cleanUrl = Uri.parse(url).buildUpon().clearQuery().build().toString()
        val request = Request.Builder().url(cleanUrl).build()
        return try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {

                val priorResponse = response.priorResponse

                val realFileName = priorResponse?.let {
                    Uri.parse(it.headers["location"]).buildUpon().clearQuery().build().toString().split("/").last()
                } ?: fileName

                val file = File(context.cacheDir, realFileName)

                if (file.exists()) {
                    println("dion: File already exists")
                    return file
                }

                val inputStream = response.body?.byteStream()
                val outputStream = file.outputStream()

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                println("dion: Downloaded file")
                file
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Upload the podcast file to OpenAI and return the transcription
    fun postAudioTranscription(file: File): String? {
        val url = "https://api.openai.com/v1/audio/transcriptions"
        val client = OkHttpClient.Builder()
            .connectTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .build()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("model", "whisper-1")
            .addFormDataPart("response_format", "srt")
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer ${BuildConfig.OPEN_AI_TOKEN}")
            .post(requestBody)
            .build()


        val response = client.newCall(request).execute()

        return if (response.isSuccessful) {
            println("dion: File uploaded successfully")
            response.body?.string()
        } else {
            println("dion: Error uploading file: ${response.code} ${response.message}")
            null
        }
    }

    suspend fun fecthRssPodcast(rssSource: String): PodcastSearchDto {
        return withContext(Dispatchers.IO) {
            val iTChannel = Reader.coRead<ITunesChannel>(rssSource)
            val results = iTChannel.items?.take(14)?.map { iTItemData ->
                EpisodeDto(
                    id = iTItemData.guid?.value ?: "",
                    link = iTItemData.link ?: "",
                    audio = iTItemData.enclosure?.url ?: "",
                    image = iTItemData.image ?: "",
                    podcast = PodcastDto(
                        id = iTItemData.episode.toString(),
                        image = iTItemData.image ?: "",
                        thumbnail = iTItemData.image ?: "",
                        listennotesURL = "",
                        titleOriginal = iTItemData.title ?: "",
                        publisherOriginal = iTItemData.author ?: "",
                    ),
                    thumbnail = iTItemData.image ?: "",
                    pubDateMS = iTItemData.pubDate?.toDate()?.time ?: 0,
                    titleOriginal = iTItemData.title ?: "",
                    listennotesURL = iTItemData.link ?: "",
                    audioLengthSec = iTItemData.duration?.toLong() ?: 0,
                    explicitContent = iTItemData.explicit ?: false,
                    descriptionOriginal = decodeDescription(iTItemData.description ?: ""),
                )
            } ?: emptyList()

            return@withContext PodcastSearchDto(
                count = iTChannel.items?.size?.toLong() ?: 0,
                total = iTChannel.items?.size?.toLong() ?: 0,
                results = results
            )
        }
    }

    // Todo: This function is only for tech crunch feeds, this should be moved to domain layer.
    private fun decodeDescription(descriptionOrigin: String): String {
        val descriptionDecode = HtmlCompat.fromHtml(
            descriptionOrigin,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toString()
        val descriptionResult = StringBuilder()
        descriptionDecode.split(";").withIndex().forEach { (index, singleNews) ->
            descriptionResult.append("${index + 1}. $singleNews\n")
        }
        return descriptionResult.toString()
    }
}