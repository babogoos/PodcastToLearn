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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.domain.model.Caption
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
    val podcastCaptions = podcastLyricsViewModel.podcastCaptions
    val title = "..."
    var titlwWithState by remember {
        mutableStateOf(title)
    }
    val navController = Navigator.current
    var captions by remember {
        mutableStateOf(listOf<Caption>())
    }
    var currentIndex by remember {
        mutableStateOf(0)
    }

    Surface {
        when (podcastCaptions) {
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
                        when (podcastCaptions) {
                            is Resource.Success -> {
                                navController.navigate(
                                    Destination.dailyWord(
                                        title = podcastCaptions.data.title,
                                        articleVaule = podcastCaptions.data.captions.joinToString("") { it.captionText },
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
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.podcast_bottom_bar_height))
                    .fillMaxSize()
            ) {

                when (podcastCaptions) {
                    is Resource.Error -> {
                        item {
                            ErrorView(text = podcastCaptions.failure.translate()) {
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
                        captions = podcastCaptions.data.captions
                        val timestamp = podcastLyricsViewModel.currentPlaybackPosition
                        currentIndex = captions.indexOfFirst { it.start <= timestamp && it.end >= timestamp }
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
                                val data = podcastCaptions.data
                                titlwWithState = data.title
                                data.captions.forEachIndexed { index, lyric ->
                                    Text(
                                        text = lyric.captionText,
                                        color = if (index == currentIndex) {
                                            Color.Green
                                        } else {
                                            Color.Unspecified
                                        },
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

    LaunchedEffect("playbackPosition") {
        podcastLyricsViewModel.updateCurrentPlaybackPosition()
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