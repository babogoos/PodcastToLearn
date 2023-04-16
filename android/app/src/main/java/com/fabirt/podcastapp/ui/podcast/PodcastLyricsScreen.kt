package com.fabirt.podcastapp.ui.podcast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabirt.podcastapp.ui.common.PreviewContent
import com.fabirt.podcastapp.ui.common.ViewModelProvider
import com.fabirt.podcastapp.ui.home.ErrorView
import com.fabirt.podcastapp.ui.home.ProgressLoadingPlaceholder
import com.fabirt.podcastapp.ui.navigation.Destination
import com.fabirt.podcastapp.ui.navigation.Navigator
import com.fabirt.podcastapp.util.Resource

/**
 * Created by dion on 2023/04/12.
 */

@Composable
fun PodcastLyricsScreen(url: String, fileName: String) {
    val scrollState = rememberLazyListState()
    val podcastLyricsViewModel = ViewModelProvider.podcastLyrics
    val podcastLyrics = podcastLyricsViewModel.podcastLyrics
    val title = "..."
    var titlwWithState by remember {
        mutableStateOf(title)
    }
    val navController = Navigator.current

    println("dion: PodcastLyricsScreen")

    Surface {
        when (podcastLyrics) {
            is Resource.Loading -> {
                podcastLyricsViewModel.fetchPodcastLyrics(url, fileName)
            }

            is Resource.Success -> {

            }

            else -> {
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 24.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Title: $titlwWithState", modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
                Button(
                    onClick = {
                        when (podcastLyrics) {
                            is Resource.Success -> {
                                navController.navigate(
                                    Destination.dailyWord(
                                        title = podcastLyrics.data.title,
                                        articleVaule = podcastLyrics.data.lyrics.joinToString(""),
                                    )
                                )
                            }

                            else -> {

                            }
                        }
                    },
                    shape = RoundedCornerShape(50),
                ) {
                    Text(text = "Today's Word", fontSize = 14.sp)
                }
            }

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
                            ProgressLoadingPlaceholder()
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
                                val data = podcastLyrics.data
                                titlwWithState = data.title
                                data.lyrics.forEach { lyric ->
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