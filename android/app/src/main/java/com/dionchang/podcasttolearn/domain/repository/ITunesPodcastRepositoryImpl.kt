package com.dionchang.podcasttolearn.domain.repository

import com.dionchang.podcasttolearn.data.database.dao.ArticlesDao
import com.dionchang.podcasttolearn.data.database.model.ArticleEntity
import com.dionchang.podcasttolearn.data.datastore.PodcastDataStore
import com.dionchang.podcasttolearn.data.network.client.RSSReaderClient
import com.dionchang.podcasttolearn.domain.model.PodcastSearch
import com.dionchang.podcasttolearn.error.Failure
import com.dionchang.podcasttolearn.util.Either

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
                if (articlesDao.getArticle(it.id) == null) {
                    articlesDao.insertArticle(
                        ArticleEntity(
                            articleId = it.id,
                            pubDateMS = it.pubDateMS,
                            orginDescription = it.descriptionOriginal,
                        )
                    )
                }
            }
            Either.Right(result)
        } catch (e: Exception) {
            Either.Left(Failure.UnexpectedFailure)
        }
    }
}