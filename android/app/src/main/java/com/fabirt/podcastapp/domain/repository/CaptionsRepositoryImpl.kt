package com.fabirt.podcastapp.domain.repository

import android.content.Context
import android.net.Uri
import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.network.model.TranscriptResultDto
import com.fabirt.podcastapp.data.network.service.OpenAiService
import com.fabirt.podcastapp.data.network.service.PodcastService
import com.fabirt.podcastapp.domain.model.DailyWord
import com.fabirt.podcastapp.domain.model.PodcastCaptions
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File

class CaptionsRepositoryImpl(
    @ApplicationContext val context: Context,
    private val podcastService: PodcastService,
    private val openAiService: OpenAiService,
    private val dataStore: PodcastDataStore
) : CaptionsRepository {

    // Upload the podcast file to OpenAI and return the captions
    override suspend fun fetchPodcastCaptions(url: String, audioId: String): Either<Failure, PodcastCaptions> {
        try {
            return withContext(Dispatchers.IO) {
                dataStore.readTranscriptResult(audioId)?.let {
                    println("dion: Already fetched captions")
                    return@withContext Either.Right(it)
                }
                val audioFile = getPodcatAudioFile(url, audioId)
                println("dion: Transcripting Captions: File Name= ${audioFile.name}, File length= ${audioFile.length()}")
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    audioFile.name,
                    audioFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )

                openAiService.audioTranscriptions(file = filePart).let { response ->
                    println("dion: Transcript success")
                    val result = response.body()?.string()!!
                    val podcastCaptions = PodcastCaptions(audioId, TranscriptResultDto(result).asDomainModel())
                    dataStore.storeTranscriptResult(podcastCaptions)
                    Either.Right(podcastCaptions)
                }
            }

        } catch (e: Exception) {
            println("dion: Error fetching captions, error message: ${e.message}")
            return Either.Left(Failure.UnexpectedFailure)
        }

    }

    private suspend fun getPodcatAudioFile(url: String, audioId: String): File {
        val audioDir = File(context.cacheDir, "audio").also {
            if (!it.exists()) {
                it.mkdir()
            }
        }

        File(audioDir, audioId).also { audioIdDir ->
            return if (audioIdDir.exists()) {
                println("dion: Audio file already exists")
                audioIdDir.listFiles()?.first()!!
            } else {
                downloadAudioFile(url, audioId, audioIdDir)
            }
        }
    }

    private suspend fun downloadAudioFile(url: String, audioId: String, audioIdDir: File): File {
        println("dion: Audio file downloading...")
        val response = podcastService.downloadAudioFile(url)
        val realFileName = (response.raw() as Response).priorResponse?.let { priorResponse ->
            Uri.parse(priorResponse.headers["location"]).buildUpon().clearQuery().build().toString().split("/").last()
        } ?: ("${audioId}.mp3")

        val inputStream = response.body()?.byteStream()

        audioIdDir.mkdir()
        val file = File(audioIdDir, realFileName)
        println("dion: Audio file saving...")
        file.outputStream().use { fileOut ->
            inputStream?.use { input ->
                input.copyTo(fileOut)
            }
        }
        println("dion: Audio file downloaded")
        return file
    }

    override suspend fun getDailyWord(title: String, article: String): Either<Failure, DailyWord> {
        TODO("Not yet implemented")
    }
}