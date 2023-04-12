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

/**
 * Created by dion on 2023/04/12.
 */

@Composable
fun PodcastLyricsScreen(
    podcastId: String,
    lyrics: List<String> = listOf("Hello", "World", "This", "Is", "A", "Test")
) {
    val scrollState = rememberLazyListState()
    Surface {
        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            lyrics.forEach {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = it,
                        )
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
        PodcastLyricsScreen(podcastId = "123")
    }
}

@Preview(name = "PodcastLyrics (Dark)")
@Composable
fun PodcastLyricsScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        PodcastLyricsScreen(podcastId = "123")
    }
}