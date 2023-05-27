package com.fabirt.podcastapp.ui.podcast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.domain.model.Caption
import com.fabirt.podcastapp.domain.model.OptionsQuiz
import com.fabirt.podcastapp.domain.model.PodcastCaptions
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
    val dialogOpen = remember { mutableStateOf(false) }
    val onWordsClick: (podcastCaptions: PodcastCaptions) -> Unit = {
        openDailyWords(navController, it)
    }
    val fetchPodcastCaptions: () -> Unit = {
        podcastCaptionsViewModel.fetchPodcastCaptions(url, audioId)
    }
    val getOptionsQuizzes: () -> Unit = {
        podcastCaptionsViewModel.getOptionsQuizzes(audioId)
    }
    val timestamp = podcastCaptionsViewModel.currentPlaybackPosition

    PodcastCaptionsContent(
        podcastCaptions = podcastCaptions,
        fetchPodcastCaptions = fetchPodcastCaptions,
        optionsQuizzes = optionsQuizzes,
        dialogOpen = dialogOpen,
        getOptionsQuizzes = getOptionsQuizzes,
        title = title,
        onWordsClick = onWordsClick,
        scrollState = scrollState,
        timestamp = timestamp
    )

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
private fun PodcastCaptionsContent(
    podcastCaptions: Resource<PodcastCaptions>,
    optionsQuizzes: Resource<List<OptionsQuiz>>,
    dialogOpen: MutableState<Boolean>,
    scrollState: LazyListState,
    title: String,
    timestamp: Long,
    fetchPodcastCaptions: () -> Unit = {},
    getOptionsQuizzes: () -> Unit = {},
    onWordsClick: (podcastCaptions: PodcastCaptions) -> Unit = {},
) {
    Surface {
        when (podcastCaptions) {
            is Resource.Loading -> {
                fetchPodcastCaptions()
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
                horizontalArrangement = Arrangement.Center
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val (buttonQuiz, textTitle, buttonWords) = createRefs()
                    Button(
                        enabled = podcastCaptions is Resource.Success,
                        onClick = {
                            when (optionsQuizzes) {
                                is Resource.Success -> {
                                    dialogOpen.value = true
                                }

                                is Resource.Error -> {
                                    getOptionsQuizzes()
                                }

                                else -> {
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.constrainAs(buttonQuiz) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
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

                    Text(
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .constrainAs(textTitle) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(buttonQuiz.end)
                                end.linkTo(buttonWords.start)
                                width = Dimension.fillToConstraints
                            }
                            .padding(12.dp),
                    )

                    Button(
                        enabled = podcastCaptions is Resource.Success,
                        onClick = {
                            (podcastCaptions as Resource.Success).data.let {
                                onWordsClick.invoke(it)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.constrainAs(buttonWords) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                    ) {
                        Text(text = "Words", fontSize = 14.sp)
                    }
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
                                fetchPodcastCaptions()
                            }
                        }
                    }

                    Resource.Loading -> {
                        item {
                            ProgressLoadingPlaceholder()
                        }
                    }

                    is Resource.Success -> {
                        val currentIndex = podcastCaptions.data.captions.indexOfFirst { it.start <= timestamp && it.end >= timestamp }
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
}


private fun openDailyWords(navController: NavHostController, it: PodcastCaptions) {
    navController.navigate(
        Destination.dailyWord(
            audioId = it.audioId,
            articleVaule = it.captions.joinToString { caption -> caption.captionText },
        )
    )
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
        val dialogOpen = remember {
            mutableStateOf(false)
        }
        PodcastCaptionsPreview(dialogOpen)
    }
}

@Preview(name = "PodcastCaptions (Dark)")
@Composable
fun PodcastCaptionsScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        val dialogOpen = remember {
            mutableStateOf(false)
        }
        PodcastCaptionsPreview(dialogOpen)
    }
}

@Composable
private fun PodcastCaptionsPreview(dialogOpen: MutableState<Boolean>) {
    PodcastCaptionsContent(
        podcastCaptions = Resource.Success(
            PodcastCaptions(
                audioId = "123",
                captions = listOf(
                    Caption(
                        captionText = "123",
                        start = 0,
                        end = 1000,
                        index = 0
                    ),
                    Caption(
                        captionText = "456",
                        start = 1000,
                        end = 2000,
                        index = 1
                    ),
                    Caption(
                        captionText = "789",
                        start = 2000,
                        end = 3000,
                        index = 2
                    ),
                )
            )
        ),
        optionsQuizzes = Resource.Success(
            listOf()
        ),
        dialogOpen = dialogOpen,
        scrollState = rememberLazyListState(),
        title = "Title Hear",
        timestamp = 0
    )
}