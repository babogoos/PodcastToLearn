package com.dionchang.podcasttolearn.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dionchang.podcasttolearn.domain.model.Word
import com.dionchang.podcasttolearn.domain.repository.ArticleRepository
import com.dionchang.podcasttolearn.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by dion on 2023/04/16.
 */
@HiltViewModel
class DailyWordViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val article = checkNotNull(savedStateHandle.get<String>(KEY_ARTICLE))
    val audioId = checkNotNull(savedStateHandle.get<String>(KEY_AUDIO_ID))
    var dailyWord by mutableStateOf<Resource<List<Word>>>(Resource.Loading)
        private set

    init {
        getDailyWord(audioId, article)
    }

    fun getDailyWord(audioId: String, article: String) {
        viewModelScope.launch {
            dailyWord = Resource.Loading
            articleRepository.getDailyWord(audioId, article)
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
        const val KEY_AUDIO_ID = "audioId"
    }
}

