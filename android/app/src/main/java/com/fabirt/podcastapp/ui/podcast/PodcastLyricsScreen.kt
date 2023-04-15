package com.fabirt.podcastapp.ui.podcast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabirt.podcastapp.ui.common.PreviewContent
import com.fabirt.podcastapp.ui.common.ViewModelProvider
import com.fabirt.podcastapp.ui.home.ErrorView
import com.fabirt.podcastapp.ui.home.LyricsLoadingPlaceholder
import com.fabirt.podcastapp.util.Resource

/**
 * Created by dion on 2023/04/12.
 */

@Composable
fun PodcastLyricsScreen(url: String, fileName: String) {

    val scrollState = rememberLazyListState()
    val podcastLyricsViewModel = ViewModelProvider.podcastLyrics
    val podcastLyrics = podcastLyricsViewModel.podcastLyrics

    println("dion: PodcastLyricsScreen")

    Surface {
        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 24.dp)
                .fillMaxSize()
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
                        LyricsLoadingPlaceholder()
                    }
                    item {
                        Button(
                            onClick = {
                                podcastLyricsViewModel.fetchPodcastLyrics(url, fileName)
                            },
                        ) {
                            Text(text = "Download Lyrics")
                        }
                    }
                }

                is Resource.Success -> {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 12.dp
                                )
                                .fillMaxSize()
                        ) {
                            podcastLyrics.data.lyrics.forEach { lyric ->
                                Text(
                                    text = lyric,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
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