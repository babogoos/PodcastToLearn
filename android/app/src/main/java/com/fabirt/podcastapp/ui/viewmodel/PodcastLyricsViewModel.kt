package com.fabirt.podcastapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabirt.podcastapp.constant.K
import com.fabirt.podcastapp.data.service.MediaPlayerServiceConnection
import com.fabirt.podcastapp.domain.model.PodcastLyrics
import com.fabirt.podcastapp.domain.repository.PodcastRepository
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
class PodcastLyricsViewModel @Inject constructor(
    private val iTunesPodcastRepository: PodcastRepository,
    serviceConnection: MediaPlayerServiceConnection,
) : ViewModel() {
    var podcastLyrics by mutableStateOf<Resource<PodcastLyrics>>(Resource.Loading)
        private set

    var currentPlaybackPosition by mutableStateOf(0L)
    private val playbackState = serviceConnection.playbackState
    val playbackPositionFlow = snapshotFlow { currentPlaybackPosition }

    fun fetchPodcastLyrics(url: String, fileName: String) {
        println("dion: fetchPodcastLyrics")
        viewModelScope.launch {
            podcastLyrics = Resource.Loading
            val file = iTunesPodcastRepository.downloadFile(url, fileName)
            val result = iTunesPodcastRepository.fetchPodcastLyrics(file!!)
            result.fold(
                { failure ->
                    podcastLyrics = Resource.Error(failure)
                },
                { data ->
                    podcastLyrics = Resource.Success(data)
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
        podcastLyrics = Resource.Loading
    }
}