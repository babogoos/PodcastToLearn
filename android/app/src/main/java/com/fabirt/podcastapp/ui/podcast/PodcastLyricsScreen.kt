package com.fabirt.podcastapp.ui.podcast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fabirt.podcastapp.ui.common.PreviewContent
import com.fabirt.podcastapp.ui.common.ViewModelProvider
import com.fabirt.podcastapp.ui.home.ErrorView
import com.fabirt.podcastapp.ui.home.LoadingPlaceholder
import com.fabirt.podcastapp.util.Resource

/**
 * Created by dion on 2023/04/12.
 */

@Composable
fun PodcastLyricsScreen(url: String, fileName: String) {

    val scrollState = rememberLazyListState()
    val podcastLyricsViewModel = ViewModelProvider.podcastLyrics
    val podcastLyrics = podcastLyricsViewModel.podcastLyrics

    podcastLyricsViewModel.fetchPodcastLyrics(url, fileName)

    Surface {
        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            when (podcastLyrics) {
                is Resource.Error -> {
                    item {
                        ErrorView(text = podcastLyrics.failure.translate()) {
                            podcastLyricsViewModel.fetchPodcastLyrics(url, fileName)
                        }
                    }
                }
                Resource.Loading -> {
                    item {
                        LoadingPlaceholder()
                    }
                }
                is Resource.Success -> {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            podcastLyrics.data.lyrics.forEach { lyric ->
                                Text(text = lyric)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "PodcastLyrics")
@Composable
fun PodcastLyricsScreenPreview() {
    PreviewContent {
        PodcastLyricsScreen(url = "https://www.google.com", fileName = "123")
    }
}

@Preview(name = "PodcastLyrics (Dark)")
@Composable
fun PodcastLyricsScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        PodcastLyricsScreen(url = "https://www.google.com", fileName = "123")
    }
}