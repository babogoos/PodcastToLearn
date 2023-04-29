package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.network.client.RSSReaderClient
import com.fabirt.podcastapp.domain.model.DailyWord
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either

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