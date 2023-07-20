package com.dionchang.podcasttolearn.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dionchang.podcasttolearn.domain.model.Episode
import com.dionchang.podcasttolearn.domain.model.Podcast
import com.dionchang.podcasttolearn.domain.model.PodcastChannel
import com.dionchang.podcasttolearn.domain.model.PodcastSearch
import com.dionchang.podcasttolearn.ui.common.PreviewContent
import com.dionchang.podcasttolearn.ui.common.StaggeredVerticalGrid
import com.dionchang.podcasttolearn.ui.common.ViewModelProvider
import com.dionchang.podcasttolearn.ui.navigation.Destination
import com.dionchang.podcasttolearn.ui.navigation.Navigator
import com.dionchang.podcasttolearn.util.Resource
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import org.jetbrains.annotations.TestOnly

@Composable
fun HomeScreen() {
    val scrollState = rememberLazyListState()
    val navController = Navigator.current
    val podcastSearchViewModel = ViewModelProvider.podcastSearch
    val podcastSearch = podcastSearchViewModel.podcastSearch
    val podcastCaptionsViewModel = ViewModelProvider.podcastCaptions
    podcastCaptionsViewModel.reset()
    val podcastChannels = podcastSearchViewModel.getPodcastChannels()
    val selectChannel = remember {
        mutableStateOf(podcastChannels.first())
    }
    HomeScreenContent(
        scrollState,
        podcastSearch,
        navController,
        podcastChannels,
        selectChannel,
        onRetryClick = { podcastSearchViewModel.searchPodcasts(selectChannel.value.rssLink) }
    )
}

@Composable
private fun HomeScreenContent(
    scrollState: LazyListState,
    podcastSearch: Resource<PodcastSearch>,
    navController: NavHostController,
    podcastChannels: List<PodcastChannel>,
    selectChannel: MutableState<PodcastChannel>,
    onRetryClick: () -> Unit = {},
) {
    Surface {
        LazyColumn(state = scrollState) {
            item {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 32.dp)
                        .wrapContentHeight()
                ) {
                    ExposedDropdownMenuSample(
                        options = podcastChannels.map { it.name },
                        expanded = remember { mutableStateOf(false) },
                        selectedOptionText = remember { mutableStateOf(podcastChannels.first().name) }) { index ->
                        selectChannel.value = podcastChannels[index]
                        onRetryClick.invoke()
                    }
                }

            }

            when (podcastSearch) {
                is Resource.Error -> {
                    item {
                        ErrorView(text = podcastSearch.failure.translate()) {
                            onRetryClick.invoke()
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
                        StaggeredVerticalGrid(
                            crossAxisCount = 2,
                            spacing = 16.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            podcastSearch.data.results.forEach { podcast ->
                                PodcastView(
                                    podcast = podcast,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    openPodcastDetail(navController, podcast)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp)
                        .padding(bottom = if (ViewModelProvider.podcastPlayer.currentPlayingEpisode.value != null) 64.dp else 0.dp)
                )
            }
        }
    }
}

private fun openPodcastDetail(
    navController: NavHostController,
    podcast: Episode
) {
    navController.navigate(Destination.podcast(podcast.id)) { }
}

@Composable
@Preview(name = "Home (Dark)")
fun HomeScreenDarkPreview() {
    val episodeList = mockEpisodes()
    PreviewContent(darkTheme = true) {
        HomeScreenContent(
            rememberLazyListState(),
            Resource.Success(
                PodcastSearch(episodeList.size.toLong(), episodeList.size.toLong(), episodeList)
            ),
            Navigator.current,
            listOf(
                PodcastChannel(
                    name = "Podcast Name",
                    rssLink = "https://rss.art19.com/the-daily"
                ),
            ),
            remember {
                mutableStateOf(
                    PodcastChannel(
                        name = "Name",
                        rssLink = "https://rss.art19.com/the-daily"
                    )
                )
            }
        )
    }
}


@Composable
@Preview(name = "Home")
fun HomeScreenPreview() {
    PreviewContent {
        HomeScreenContent(rememberLazyListState(), Resource.Loading, Navigator.current, listOf(), remember {
            mutableStateOf(
                PodcastChannel(
                    name = "Name",
                    rssLink = "https://rss.art19.com/the-daily"
                )
            )
        })
    }
}

@TestOnly
private fun mockEpisodes() = listOf(
    Episode(
        id = "1",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "1",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "2",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "2",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "3",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "3",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "4",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "4",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "5",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "5",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "6",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "6",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "7",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "7",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
    Episode(
        id = "8",
        audio = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        image = "https://picsum.photos/200/300",
        link = "https://picsum.photos/200/300",
        pubDateMS = 0L,
        thumbnail = "https://picsum.photos/200/300",
        titleOriginal = "Title",
        listennotesURL = "https://picsum.photos/200/300",
        audioLengthSec = 0L,
        explicitContent = false,
        descriptionOriginal = "Description",
        podcast = Podcast(
            id = "8",
            image = "https://picsum.photos/200/300",
            thumbnail = "https://picsum.photos/200/300",
            titleOriginal = "Title",
            listennotesURL = "https://picsum.photos/200/300",
            publisherOriginal = "Publisher",
        )
    ),
)
