package com.dionchang.podcasttolearn.ui.podcast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dionchang.podcasttolearn.R
import com.dionchang.podcasttolearn.domain.model.Episode
import com.dionchang.podcasttolearn.ui.common.BackButton
import com.dionchang.podcasttolearn.ui.common.EmphasisText
import com.dionchang.podcasttolearn.ui.common.PrimaryButton
import com.dionchang.podcasttolearn.ui.common.ViewModelProvider
import com.dionchang.podcasttolearn.ui.navigation.Destination
import com.dionchang.podcasttolearn.ui.navigation.Navigator
import com.dionchang.podcasttolearn.util.Resource
import com.dionchang.podcasttolearn.util.formatMillisecondsAsDate
import com.dionchang.podcasttolearn.util.toDurationMinutes
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun PodcastDetailScreen(
    podcastId: String,
) {
    val scrollState = rememberScrollState()
    val podcastSearchViewModel = ViewModelProvider.podcastSearch
    val detailViewModel = ViewModelProvider.podcastDetail
    val playerViewModel = ViewModelProvider.podcastPlayer
    val podcast = podcastSearchViewModel.getPodcastDetail(podcastId)
    val navController = Navigator.current
    val currentContext = LocalContext.current

    Surface {
        Column(
            modifier = Modifier
                .statusBarsPadding()
        ) {
            Row {
                BackButton()
            }

            if (podcast != null) {
                val playButtonText =
                    if (playerViewModel.podcastIsPlaying &&
                        playerViewModel.currentPlayingEpisode.value?.id == podcast.id
                    ) stringResource(R.string.pause) else stringResource(R.string.play)

                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .navigationBarsPadding()
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                        .padding(bottom = if (playerViewModel.currentPlayingEpisode.value != null) 64.dp else 0.dp)

                ) {
                    PodcastImage(
                        url = podcast.image,
                        modifier = Modifier.height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        podcast.titleOriginal,
                        style = MaterialTheme.typography.h1
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        podcast.podcast.publisherOriginal,
                        style = MaterialTheme.typography.body1
                    )

                    EmphasisText(
                        text = "${podcast.pubDateMS.formatMillisecondsAsDate("MMM dd")} • ${podcast.audioLengthSec.toDurationMinutes()}"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        PrimaryButton(
                            text = playButtonText,
                            height = 48.dp
                        ) {
                            playerViewModel.playPodcast(
                                (podcastSearchViewModel.podcastSearch as Resource.Success).data.results,
                                podcast
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        com.dionchang.podcasttolearn.ui.common.IconButton(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share)
                        ) {
                            detailViewModel.sharePodcastEpidose(currentContext, podcast)
                        }

                        com.dionchang.podcasttolearn.ui.common.IconButton(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = stringResource(R.string.source_web)
                        ) {
                            detailViewModel.openListenNotesURL(currentContext, podcast)
                        }

                        com.dionchang.podcasttolearn.ui.common.IconButton(
                            imageVector = ImageVector.vectorResource(id = R.drawable.transcribe),
                            contentDescription = stringResource(R.string.fech_captions_with_whisper_api)
                        ) {
                            openPodcastCaptions(
                                navController = navController,
                                episode = podcast
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EmphasisText(text = podcast.descriptionOriginal)
                }
            }
        }
    }
}

private fun openPodcastCaptions(
    navController: NavHostController,
    episode: Episode
) {
    val title = episode.titleOriginal
    val audioId = episode.id
    val audioUrl = episode.audio
    navController.navigate(Destination.captions(title, audioUrl, audioId)) { }
}