package com.dionchang.podcasttolearn.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dionchang.podcasttolearn.ui.viewmodel.PodcastCaptionsViewModel
import com.dionchang.podcasttolearn.ui.viewmodel.PodcastDetailViewModel
import com.dionchang.podcasttolearn.ui.viewmodel.PodcastPlayerViewModel
import com.dionchang.podcasttolearn.ui.viewmodel.PodcastSearchViewModel

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

    val podcastCaptions: PodcastCaptionsViewModel
        @Composable
        get() = LocalPodcastCaptionsViewModel.current
}

@Composable
fun ProvideMultiViewModel(content: @Composable () -> Unit) {
    val viewModel1: PodcastSearchViewModel = viewModel()
    val viewModel2: PodcastDetailViewModel = viewModel()
    val viewModel3: PodcastPlayerViewModel = viewModel()
    val viewModel4: PodcastCaptionsViewModel = viewModel()

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
                    LocalPodcastCaptionsViewModel provides viewModel4,
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

private val LocalPodcastCaptionsViewModel = staticCompositionLocalOf<PodcastCaptionsViewModel> {
    error("No PodcastCaptionsViewModel provided")
}