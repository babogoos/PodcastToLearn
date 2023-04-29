package com.fabirt.podcastapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabirt.podcastapp.constant.K
import com.fabirt.podcastapp.data.service.MediaPlayerServiceConnection
import com.fabirt.podcastapp.domain.model.PodcastCaptions
import com.fabirt.podcastapp.domain.repository.ArticleRepository
import com.fabirt.podcastapp.util.Resource
import com.fabirt.podcastapp.util.currentPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by dion on 2023/04/13.
 */
@HiltViewModel
class PodcastCaptionsViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    serviceConnection: MediaPlayerServiceConnection,
) : ViewModel() {
    var podcastCaptions by mutableStateOf<Resource<PodcastCaptions>>(Resource.Loading)
        private set

    var currentPlaybackPosition by mutableStateOf(0L)
    private val playbackState = serviceConnection.playbackState

    fun fetchPodcastCaptions(url: String, audioId: String) {
        println("dion: fetchPodcastCaptions")
        viewModelScope.launch {
            podcastCaptions = Resource.Loading
            articleRepository.fetchPodcastCaptions(url, audioId).fold(
                { failure ->
                    podcastCaptions = Resource.Error(failure)
                },
                { data ->
                    podcastCaptions = Resource.Success(data)
                }
            )
        }
    }

    suspend fun updateCurrentPlaybackPosition() {
        val currentPosition = playbackState.value?.currentPosition
        if (currentPosition != null && currentPosition != currentPlaybackPosition) {
            currentPlaybackPosition = currentPosition
        }
        delay(K.PLAYBACK_POSITION_UPDATE_INTERVAL)
        updateCurrentPlaybackPosition()
    }


    fun reset() {
        podcastCaptions = Resource.Loading
    }
}