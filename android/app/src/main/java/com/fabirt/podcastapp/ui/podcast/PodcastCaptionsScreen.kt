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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.domain.model.Caption
import com.fabirt.podcastapp.domain.model.OptionsQuiz
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
fun PodcastCaptionsScreen(url: String, title: String, audioId: String) {
    val scrollState = rememberLazyListState()
    val podcastCaptionsViewModel = ViewModelProvider.podcastCaptions
    val podcastCaptions = podcastCaptionsViewModel.podcastCaptions
    val optionsQuizzes = podcastCaptionsViewModel.optionsQuizzes
    val navController = Navigator.current
    var captions by remember { mutableStateOf(listOf<Caption>()) }
    var currentIndex by remember { mutableStateOf(0) }
    val dialogOpen = remember { mutableStateOf(false) }

    Surface {
        when (podcastCaptions) {
            is Resource.Loading -> {
                podcastCaptionsViewModel.fetchPodcastCaptions(url, audioId)
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
                Text(text = title, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)

                Button(
                    enabled = podcastCaptions is Resource.Success,
                    onClick = {
                        when (optionsQuizzes) {
                            is Resource.Success -> {
                                dialogOpen.value = true
                            }

                            is Resource.Error -> {
                                podcastCaptionsViewModel.getOptionsQuizzes(audioId)
                            }

                            else -> {
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                ) {
                    when (optionsQuizzes) {
                        is Resource.Loading -> {
                            Text(text = "Loading...", fontSize = 14.sp)
                        }

                        is Resource.Error -> {
                            Text(text = "Error", fontSize = 14.sp)
                        }

                        is Resource.Success -> {
                            Text(text = "Quiz", fontSize = 14.sp)
                        }
                    }
                }

                Button(
                    enabled = podcastCaptions is Resource.Success,
                    onClick = {
                        (podcastCaptions as Resource.Success).data.let {
                            navController.navigate(
                                Destination.dailyWord(
                                    audioId = it.audioId,
                                    articleVaule = it.captions.joinToString { caption -> caption.captionText },
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text(text = "Words", fontSize = 14.sp)
                }
            }

            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = dimensionResource(id = R.dimen.podcast_bottom_bar_height))
                    .fillMaxSize()
            ) {

                when (podcastCaptions) {
                    is Resource.Error -> {
                        item {
                            ErrorView(text = podcastCaptions.failure.translate()) {
                                podcastCaptionsViewModel.fetchPodcastCaptions(url, audioId)
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
                        val timestamp = podcastCaptionsViewModel.currentPlaybackPosition
                        currentIndex = captions.indexOfFirst { it.start <= timestamp && it.end >= timestamp }
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(
                                        horizontal = 12.dp, vertical = 12.dp
                                    )
                                    .fillMaxSize()
                            ) {
                                val data = podcastCaptions.data
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

    optionsQuizzes.let {
        when (it) {
            is Resource.Success -> {
                QuizDialog(dialogOpen, it.data)
            }

            else -> {
            }
        }
    }

    LaunchedEffect("playbackPosition") {
        podcastCaptionsViewModel.updateCurrentPlaybackPosition()
    }
}

@Composable
private fun QuizDialog(dialogOpen: MutableState<Boolean>, optionsQuizzes: List<OptionsQuiz>) {
    if (dialogOpen.value) {
        Dialog(
            onDismissRequest = {
                dialogOpen.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                securePolicy = SecureFlagPolicy.SecureOff,
                usePlatformDefaultWidth = false,
            )
        ) {
            QuizScreen(optionsQuizzes)
        }
    }
}

@Preview(name = "PodcastCaptions")
@Composable
fun PodcastCaptionsScreenPreview() {
    PreviewContent {
        PodcastCaptionsScreen(url = "https://www.google.com", title = "123", audioId = "123")
    }
}

@Preview(name = "PodcastCaptions (Dark)")
@Composable
fun PodcastCaptionsScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        PodcastCaptionsScreen(url = "https://www.google.com", title = "123", audioId = "123")
    }
}