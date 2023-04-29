package com.fabirt.podcastapp.domain.model

/**
 * Created by dion on 2023/04/13.
 */
data class PodcastCaptions(
    val audioId: String,
    val captions: List<Caption>,
    val title: String = "",
)
