package com.fabirt.podcastapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabirt.podcastapp.domain.model.PodcastLyrics
import com.fabirt.podcastapp.domain.repository.PodcastRepository
import com.fabirt.podcastapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by dion on 2023/04/13.
 */
@HiltViewModel
class PodcastLyricsViewModel @Inject constructor(
    private val iTunesPodcastRepository: PodcastRepository
) : ViewModel() {
    var podcastLyrics by mutableStateOf<Resource<PodcastLyrics>>(Resource.Loading)
        private set

    fun fetchPodcastLyrics(url: String, fileName: String) {
        viewModelScope.launch {
            podcastLyrics = Resource.Loading
            val result = iTunesPodcastRepository.fetchPodcastLyrics(url, fileName)
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
}