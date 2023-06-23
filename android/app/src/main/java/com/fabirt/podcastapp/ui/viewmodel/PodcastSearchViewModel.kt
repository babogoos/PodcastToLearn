package com.fabirt.podcastapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.domain.model.PodcastChannel
import com.fabirt.podcastapp.domain.model.PodcastSearch
import com.fabirt.podcastapp.domain.repository.PodcastRepository
import com.fabirt.podcastapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TECH_CRUNCH_DAILY_PODCAST_RSS =
    "https://www.omnycontent.com/d/playlist/207a2356-7ea1-423e-909e-aea100c537cf/82cf261f-dd6f-4ffd-ab8c-afbe011396ed/a8961ccc-f44e-4587-a33f-afbe011396fb/podcast.rss"
private const val TAIWAN_PUBLIC_TELEVISION_SERVICE_PODCAST_RSS = "https://anchor.fm/s/27b2c13c/podcast/rss"

@HiltViewModel
class PodcastSearchViewModel @Inject constructor(
    private val repository: PodcastRepository
) : ViewModel() {

    var podcastSearch by mutableStateOf<Resource<PodcastSearch>>(Resource.Loading)
        private set

    init {
        searchPodcasts(getPodcastChannels().first().rssLink)
    }

    fun getPodcastDetail(id: String): Episode? {
        return when (podcastSearch) {
            is Resource.Error -> null
            Resource.Loading -> null
            is Resource.Success -> (podcastSearch as Resource.Success<PodcastSearch>).data.results.find { it.id == id }
        }
    }

    fun searchPodcasts(rssLink: String) {
        viewModelScope.launch {
            podcastSearch = Resource.Loading
            val result = repository.searchPodcasts(rssLink, "episode")
            result.fold(
                { failure ->
                    podcastSearch = Resource.Error(failure)
                },
                { data ->
                    podcastSearch = Resource.Success(data)
                }
            )
        }
    }

    fun getPodcastChannels(): List<PodcastChannel> = listOf(
        PodcastChannel(
            name = "Tech Crunch Daily",
            rssLink = TECH_CRUNCH_DAILY_PODCAST_RSS
        ),
        PodcastChannel(
            name = "Taiwan Public Television Service",
            rssLink = TAIWAN_PUBLIC_TELEVISION_SERVICE_PODCAST_RSS
        )
    )
}