package com.dionchang.podcasttolearn.domain.repository

import com.dionchang.podcasttolearn.domain.model.PodcastSearch
import com.dionchang.podcasttolearn.error.Failure
import com.dionchang.podcasttolearn.util.Either

interface PodcastRepository {

    suspend fun searchPodcasts(
        query: String,
        type: String,
    ): Either<Failure, PodcastSearch>
}