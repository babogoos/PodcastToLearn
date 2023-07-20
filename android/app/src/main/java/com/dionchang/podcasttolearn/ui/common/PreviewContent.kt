package com.dionchang.podcasttolearn.ui.common

import androidx.compose.runtime.Composable
import com.dionchang.podcasttolearn.ui.navigation.ProvideNavHostController
import com.dionchang.podcasttolearn.ui.theme.PodcastAppTheme
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun PreviewContent(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    PodcastAppTheme(darkTheme = darkTheme) {
        ProvideWindowInsets {
            ProvideNavHostController {
                content()
            }
        }
    }
}