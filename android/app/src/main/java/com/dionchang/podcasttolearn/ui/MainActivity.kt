package com.dionchang.podcasttolearn.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.dionchang.podcasttolearn.R
import com.dionchang.podcasttolearn.constant.K
import com.dionchang.podcasttolearn.ui.common.ProvideMultiViewModel
import com.dionchang.podcasttolearn.ui.home.HomeScreen
import com.dionchang.podcasttolearn.ui.navigation.Destination
import com.dionchang.podcasttolearn.ui.navigation.Navigator
import com.dionchang.podcasttolearn.ui.navigation.ProvideNavHostController
import com.dionchang.podcasttolearn.ui.podcast.PodcastBottomBar
import com.dionchang.podcasttolearn.ui.podcast.PodcastCaptionsScreen
import com.dionchang.podcasttolearn.ui.podcast.PodcastDetailScreen
import com.dionchang.podcasttolearn.ui.podcast.PodcastPlayerScreen
import com.dionchang.podcasttolearn.ui.theme.PodcastAppTheme
import com.dionchang.podcasttolearn.ui.viewmodel.DailyWordViewModel
import com.dionchang.podcasttolearn.ui.vocabulary.DailyWordScreen
import com.dionchang.podcasttolearn.ui.welcome.WelcomeScreen
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_PodcastApp)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        var startDestination = Destination.welcome
        if (intent?.action == K.ACTION_PODCAST_NOTIFICATION_CLICK) {
            startDestination = Destination.home
        }

        setContent {
            PodcastApp(
                startDestination = startDestination,
                backDispatcher = onBackPressedDispatcher
            )
        }
    }
}

@Composable
fun PodcastApp(
    startDestination: String = Destination.welcome,
    backDispatcher: OnBackPressedDispatcher
) {
    PodcastAppTheme {
        ProvideWindowInsets {
            ProvideMultiViewModel {
                ProvideNavHostController {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavHost(Navigator.current, startDestination) {
                            composable(Destination.welcome) { WelcomeScreen() }

                            composable(Destination.home) {
                                HomeScreen()
                            }

                            composable(
                                Destination.podcast,
                                deepLinks = listOf(navDeepLink {
                                    uriPattern = "https://www.listennotes.com/e/{id}"
                                })
                            ) { backStackEntry ->
                                println("dion Destination.podcast: ${backStackEntry.arguments?.getString("fileName")})")
                                PodcastDetailScreen(
                                    podcastId = backStackEntry.arguments?.getString("id")!!,
                                )
                            }

                            composable(
                                Destination.captions,
                                arguments = listOf(
                                    navArgument("url") {
                                        type = NavType.StringType
                                        defaultValue = "Default url"
                                    },
                                    navArgument("title") {
                                        type = NavType.StringType
                                        defaultValue = "Default title"
                                    },
                                    navArgument("audioId") {
                                        type = NavType.StringType
                                        defaultValue = "Default audioId"
                                    }

                                )
                            ) { backStackEntry ->
                                println("dion Destination.captions: ${backStackEntry.arguments?.getString("title")})")
                                val url = backStackEntry.arguments?.getString("url", "Default url")!!
                                val title = backStackEntry.arguments?.getString("title", "Default title")!!
                                val audioId = backStackEntry.arguments?.getString("audioId", "Default audioId")!!
                                PodcastCaptionsScreen(url, title, audioId)
                            }

                            composable(
                                Destination.dailyWord,
                                arguments = listOf(
                                    navArgument(DailyWordViewModel.KEY_ARTICLE) {
                                        type = NavType.StringType
                                        defaultValue = "Default article"
                                    },
                                    navArgument(DailyWordViewModel.KEY_AUDIO_ID) {
                                        type = NavType.StringType
                                        defaultValue = "Default audioId"
                                    },
                                )
                            ) {
                                DailyWordScreen()
                            }
                        }
                        PodcastBottomBar(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                        PodcastPlayerScreen(backDispatcher)
                    }
                }
            }
        }
    }
}

fun String.toDate(pattern: String = DateExtension.RFC2822): Date {
    return try {
        val format = SimpleDateFormat(pattern, Locale.ENGLISH)
        format.timeZone = TimeZone.getTimeZone("UTC") // UTC+0
        format.parse(this)
    } catch (e: Exception) {
        val format = SimpleDateFormat(DateExtension.RFC822, Locale.ENGLISH)
        format.timeZone = TimeZone.getTimeZone("UTC") // UTC+0
        format.parse(this)
    }
}

fun Date.toDateString(
    pattern: String = "yyyy_MM_dd",
    zone: TimeZone = TimeZone.getDefault()
): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    format.timeZone = zone
    return format.format(this)
}


object DateExtension {
    const val ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'"  // e.g. 2022-01-11T11:00:00Z
    const val RFC822 = "yyyy-MM-dd'T'HH:mm:ssZ"  // e.g. 2022-01-11T11:00:00+0800
    const val RFC2822 = "EEE, dd MMM yyyy HH:mm:ss Z"  // e.g. Tue, 04 Apr 2023 11:00:00 +0000

    const val WORDPRESS_POST_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
}