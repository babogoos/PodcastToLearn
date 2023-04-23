package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.domain.model.DailyWord
import com.fabirt.podcastapp.domain.model.PodcastCaptions
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import java.io.File

interface PodcastRepository {

    suspend fun searchPodcasts(
        query: String,
        type: String,
    ): Either<Failure, PodcastSearch>

    suspend fun fetchPodcastLyrics(file: File): Either<Failure, PodcastCaptions>
    suspend fun downloadFile(url: String, fileName: String): File?
    suspend fun getDailyWord(title: String, article: String): Either<Failure, DailyWord>
}