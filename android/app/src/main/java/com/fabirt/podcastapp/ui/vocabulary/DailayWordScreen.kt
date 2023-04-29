package com.fabirt.podcastapp.ui.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.domain.model.Word
import com.fabirt.podcastapp.ui.common.PreviewContent
import com.fabirt.podcastapp.ui.home.ErrorView
import com.fabirt.podcastapp.ui.home.ProgressLoadingPlaceholder
import com.fabirt.podcastapp.ui.viewmodel.DailyWordViewModel
import com.fabirt.podcastapp.util.Resource

/**
 * Created by dion on 2023/04/15.
 */

@Composable
fun DailyWordScreen() {
    val scrollState = rememberLazyListState()
    val dailyWordViewModel = hiltViewModel<DailyWordViewModel>()

    DailyWordScreenContent(scrollState, dailyWordViewModel.dailyWord) {
        dailyWordViewModel.getDailyWord(dailyWordViewModel.audioId, dailyWordViewModel.article)
    }
}

@Composable
private fun DailyWordScreenContent(
    scrollState: LazyListState = rememberLazyListState(),
    wordList: Resource<List<Word>> = Resource.Success(
        listOf(
            Word("apple", "蘋果", "this is an apple"),
            Word("banana", "香蕉", "this is a banana"),
            Word("orange", "橘子", "this is an orange"),
        )
    ),
    retry: () -> Unit = {}
) {
    Surface {
        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(top = 24.dp, bottom = dimensionResource(id = R.dimen.podcast_bottom_bar_height), start = 16.dp, end = 16.dp)
                .fillMaxSize()
        ) {

            when (wordList) {
                is Resource.Loading -> {
                    item {
                        ProgressLoadingPlaceholder()
                    }
                }

                is Resource.Error -> {
                    item {
                        ErrorView(text = wordList.failure.translate()) {
                            retry.invoke()
                        }
                    }
                }

                is Resource.Success -> {
                    itemsIndexed(wordList.data) { index, dailyWord ->
                        Column(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxSize()
                        ) {
                            Text(text = "Daily Word ${index + 1}")
                            DailyWordView(dailyWord)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "DailyWordScreen (Light)")
@Composable
fun DailyWordScreenPreview() {
    PreviewContent() {
        DailyWordScreenContent()
    }
}

@Preview(name = "DailyWordScreen (Dark)")
@Composable
fun DailyWordScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        DailyWordScreenContent()
    }
}

@Composable
fun DailyWordView(word: Word) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "單字：" + word.word + " (" + word.translate + ")")
        Text(text = "例句：" + word.example)
    }
}