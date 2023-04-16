package com.fabirt.podcastapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabirt.podcastapp.domain.model.Word
import com.fabirt.podcastapp.domain.repository.PodcastRepository
import com.fabirt.podcastapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by dion on 2023/04/16.
 */
@HiltViewModel
class DailyWordViewModel @Inject constructor(
    private val iTunesPodcastRepository: PodcastRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val article = checkNotNull(savedStateHandle.get<String>(KEY_ARTICLE))
    val title = checkNotNull(savedStateHandle.get<String>(KEY_TITLE))
    var dailyWord by mutableStateOf<Resource<List<Word>>>(Resource.Loading)
        private set

    init {
        getDailyWord(title, article)
    }

    fun getDailyWord(title: String, article: String) {
        viewModelScope.launch {
            dailyWord = Resource.Loading
            iTunesPodcastRepository.getDailyWord(title, article)
                .fold(
                    { failure ->
                        dailyWord = Resource.Error(failure)
                    },
                    { data ->
                        dailyWord = Resource.Success(data.words)
                    }
                )
        }
    }

    companion object {
        const val KEY_ARTICLE = "article"
        const val KEY_TITLE = "title"
    }
}

