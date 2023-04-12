package com.fabirt.podcastapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabirt.podcastapp.ui.viewmodel.PodcastDetailViewModel
import com.fabirt.podcastapp.ui.viewmodel.PodcastLyricsViewModel
import com.fabirt.podcastapp.ui.viewmodel.PodcastPlayerViewModel
import com.fabirt.podcastapp.ui.viewmodel.PodcastSearchViewModel

object ViewModelProvider {
    val podcastSearch: PodcastSearchViewModel
        @Composable
        get() = LocalPodcastSearchViewModel.current

    val podcastDetail: PodcastDetailViewModel
        @Composable
        get() = LocalPodcastDetailViewModel.current

    val podcastPlayer: PodcastPlayerViewModel
        @Composable
        get() = LocalPodcastPlayerViewModel.current

    val podcastLyrics: PodcastLyricsViewModel
        @Composable
        get() = LocalPodcastLyricsViewModel.current
}

@Composable
fun ProvideMultiViewModel(content: @Composable () -> Unit) {
    val viewModel1: PodcastSearchViewModel = viewModel()
    val viewModel2: PodcastDetailViewModel = viewModel()
    val viewModel3: PodcastPlayerViewModel = viewModel()
    val viewModel4: PodcastLyricsViewModel = viewModel()

    CompositionLocalProvider(
        LocalPodcastSearchViewModel provides viewModel1,
    ) {
        CompositionLocalProvider(
            LocalPodcastDetailViewModel provides viewModel2,
        ) {
            CompositionLocalProvider(
                LocalPodcastPlayerViewModel provides viewModel3,
            ) {
                CompositionLocalProvider(
                    LocalPodcastLyricsViewModel provides viewModel4,
                ) {
                    content()
                }
            }
        }
    }
}

private val LocalPodcastSearchViewModel = staticCompositionLocalOf<PodcastSearchViewModel> {
    error("No PodcastSearchViewModel provided")
}

private val LocalPodcastDetailViewModel = staticCompositionLocalOf<PodcastDetailViewModel> {
    error("No PodcastDetailViewModel provided")
}

private val LocalPodcastPlayerViewModel = staticCompositionLocalOf<PodcastPlayerViewModel> {
    error("No PodcastPlayerViewModel provided")
}

private val LocalPodcastLyricsViewModel = staticCompositionLocalOf<PodcastLyricsViewModel> {
    error("No PodcastLyricsViewModel provided")
}