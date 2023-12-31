package com.dionchang.podcasttolearn.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dionchang.podcasttolearn.constant.K
import com.dionchang.podcasttolearn.data.service.MediaPlayerServiceConnection
import com.dionchang.podcasttolearn.domain.model.OptionsQuiz
import com.dionchang.podcasttolearn.domain.model.PodcastCaptions
import com.dionchang.podcasttolearn.domain.repository.ArticleRepository
import com.dionchang.podcasttolearn.util.Resource
import com.dionchang.podcasttolearn.util.currentPosition
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
    private val serviceConnection: MediaPlayerServiceConnection,
) : ViewModel() {
    var podcastCaptions by mutableStateOf<Resource<PodcastCaptions>>(Resource.Loading)
        private set

    var optionsQuizzes by mutableStateOf<Resource<List<OptionsQuiz>>>(Resource.Loading)
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
                    parseArticle(audioId)
                }
            )
        }
    }

    private fun parseArticle(audioId: String) {
        optionsQuizzes = Resource.Loading
        viewModelScope.launch {
            articleRepository.parseArticle(audioId)
            articleRepository.gerenateQuiz(audioId).fold(
                { failure ->
                    optionsQuizzes = Resource.Error(failure)
                },
                { data ->
                    optionsQuizzes = Resource.Success(data)
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

    fun getOptionsQuizzes(audioId: String) {
        parseArticle(audioId)
    }

    fun playOnParagraph(paragraphId: Long) {
        viewModelScope.launch {
            articleRepository.getParagraphCaption(paragraphId)?.let {
                val position = it.start
                serviceConnection.playFromPosition(position)
            }
        }
    }
}