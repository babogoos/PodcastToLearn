package com.fabirt.podcastapp.domain.repository

import com.fabirt.podcastapp.domain.model.PodcastLyrics
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.error.Failure
import com.fabirt.podcastapp.util.Either
import java.io.File

interface PodcastRepository {

    suspend fun searchPodcasts(
        query: String,
        type: String,
    ): Either<Failure, PodcastSearch>

    suspend fun fetchPodcastLyrics(file: File): Either<Failure, PodcastLyrics>
    suspend fun downloadFile(url: String, fileName: String): File?
}