package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.data.database.dao.ArticlesDao
import com.fabirt.podcastapp.data.database.model.ArticleEntity
import com.fabirt.podcastapp.data.datastore.PodcastDataStore
import com.fabirt.podcastapp.data.network.client.RSSReaderClient
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either

/**
 * Created by dion on 2023/04/11.
 */
class ITunesPodcastRepositoryImpl(
    private val client: RSSReaderClient,
    private val dataStore: PodcastDataStore,
    private val articlesDao: ArticlesDao,
) : PodcastRepository {
    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val result = client.fecthRssPodcast(query).asDomainModel()
            dataStore.storePodcastSearchResult(result)
            result.results.forEach {
                articlesDao.insertArticle(
                    ArticleEntity(
                        articleId = it.id,
                        pubDateMS = it.pubDateMS,
                        orginDescription = it.descriptionOriginal,
                    )
                )
            }

            Either.Right(result)
        } catch (e: Exception) {
            Either.Left(Failure.UnexpectedFailure)
        }
    }
}