package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.domain.model.DailyWord
import com.fabirt.podcastapp.domain.model.PodcastCaptions
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either

/**
 * Created by dion on 2023/04/29.
 */
interface CaptionsRepository {
    suspend fun fetchPodcastCaptions(url: String, audioId: String): Either<Failure, PodcastCaptions>
    suspend fun getDailyWord(title: String, article: String): Either<Failure, DailyWord>
}