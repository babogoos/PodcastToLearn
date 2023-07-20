package com.dionchang.podcasttolearn.domain.repository

import com.dionchang.podcasttolearn.data.datastore.PodcastDataStore
import com.dionchang.podcasttolearn.data.network.service.PodcastService
import com.dionchang.podcasttolearn.domain.model.PodcastSearch
import com.dionchang.podcasttolearn.error.Failure
import com.dionchang.podcasttolearn.util.Either
import com.dionchang.podcasttolearn.util.left
import com.dionchang.podcasttolearn.util.right

class PodcastRepositoryImpl(
    private val service: PodcastService,
    private val dataStore: PodcastDataStore
) : PodcastRepository {

    companion object {
        private const val TAG = "PodcastRepository"
    }

    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val canFetchAPI = dataStore.canFetchAPI()
            if (canFetchAPI) {
                val result = service.searchPodcasts(query, type).asDomainModel()
                dataStore.storePodcastSearchResult(result)
                right(result)
            } else {
                right(dataStore.readLastPodcastSearchResult())
            }
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }
}