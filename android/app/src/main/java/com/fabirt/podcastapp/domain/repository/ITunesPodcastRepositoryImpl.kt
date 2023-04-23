package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.network.client.RSSReaderClient
import com.fabirt.podcastapp.domain.model.DailyWord
import com.fabirt.podcastapp.domain.model.PodcastCaptions
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created by dion on 2023/04/11.
 */
class ITunesPodcastRepositoryImpl(
    private val client: RSSReaderClient,
    private val dataStore: PodcastDataStore
) : PodcastRepository {
    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val result = client.fecthRssPodcast(query).asDomainModel()
            dataStore.storePodcastSearchResult(result)
            Either.Right(result)
        } catch (e: Exception) {
            Either.Left(Failure.UnexpectedFailure)
        }
    }

    override suspend fun fetchPodcastLyrics(file: File): Either<Failure, PodcastCaptions> {
        println("dion: Lyrics fetching file length ${file.length()}")
        try {
            val fileName = file.name
            return withContext(Dispatchers.IO) {
                dataStore.readTranscriptResult(fileName)?.let {
                    println("dion: Already fetched lyrics")
                    return@withContext Either.Right(it)
                }

                client.postAudioTranscription(file)?.let { transcriptResultDto ->
                    println("dion: Transcript success")
                    val podcastCaptions = PodcastCaptions(fileName, transcriptResultDto.asDomainModel())
                    dataStore.storeTranscriptResult(podcastCaptions)
                    Either.Right(podcastCaptions)
                } ?: Either.Left(Failure.UnexpectedFailure)
            }

        } catch (e: Exception) {
            println("dion: Error fetching lyrics, error message: ${e.message}")
            return Either.Left(Failure.UnexpectedFailure)
        }
    }

    override suspend fun downloadFile(url: String, fileName: String) = withContext(Dispatchers.IO) {
        client.downloadFile(url, fileName)
    }

    override suspend fun getDailyWord(title: String, article: String): Either<Failure, DailyWord> {
        return try {
            dataStore.readDailyWordResult(title)?.let {
                return Either.Right(it)
            }
            val result = client.getDailyWord(title, article)
            dataStore.storeDailyWordResult(result)
            Either.Right(result)
        } catch (e: Exception) {
            Either.Left(Failure.UnexpectedFailure)
        }
    }
}