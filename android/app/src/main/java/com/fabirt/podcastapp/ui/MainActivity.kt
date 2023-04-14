package com.fabirt.podcastapp.ui

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
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.constant.K
import com.fabirt.podcastapp.ui.common.ProvideMultiViewModel
import com.fabirt.podcastapp.ui.home.HomeScreen
import com.fabirt.podcastapp.ui.navigation.Destination
import com.fabirt.podcastapp.ui.navigation.Navigator
import com.fabirt.podcastapp.ui.navigation.ProvideNavHostController
import com.fabirt.podcastapp.ui.podcast.PodcastBottomBar
import com.fabirt.podcastapp.ui.podcast.PodcastDetailScreen
import com.fabirt.podcastapp.ui.podcast.PodcastLyricsScreen
import com.fabirt.podcastapp.ui.podcast.PodcastPlayerScreen
import com.fabirt.podcastapp.ui.theme.PodcastAppTheme
import com.fabirt.podcastapp.ui.welcome.WelcomeScreen
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

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
//        lifecycleScope.launch(Dispatchers.IO) {
//            val iTunesChannel =
//                RSSReaderClient.fecthRssPodcast("https://www.omnycontent.com/d/playlist/207a2356-7ea1-423e-909e-aea100c537cf/82cf261f-dd6f-4ffd-ab8c-afbe011396ed/a8961ccc-f44e-4587-a33f-afbe011396fb/podcast.rss")
//
//            val providePodcastDataStore = AppModule.providePodcastDataStore(applicationContext)
////            iTunesChannel.items?.sortedBy { it.duration }?.first()!!.let {
////                println(it)
////                val url = Uri.parse(it.enclosure?.url).buildUpon().clearQuery().build().toString()
////                println(url)
////                val file = RSSReaderClient.downloadFile(
////                    applicationContext,
////                    url,
////                    "DailyCrunch_" + it.pubDate?.toDate()?.toDateString() + ".mp3",
////                )
////                println(file?.name)
////                println(file?.path)
////                println(file?.length())
//
////                val openAIToken = "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"
////                val response = RSSReaderClient.postAudioTranscription(openAIToken, file!!, "whisper-1")
////
////                providePodcastDataStore.storeTranscriptResult(response!!)
//
//                println("--------postAudioTranscription----------")
//                val transcriptResult = providePodcastDataStore.readTranscriptResult()
//                val fullArticleList = mutableListOf<String>()
//                val fullArticleList4Gpt = mutableListOf<String>()
//                transcriptResult.split("\n").chunked(4).forEach { grouped ->
//                    if (grouped.size < 4) return@forEach
//                    fullArticleList.add(grouped[2])
//                    fullArticleList4Gpt.add(grouped[2].trim())
//                }
//                val fullArticle4Gpt = fullArticleList4Gpt.joinToString("")
//                println(transcriptResult)
//                println("--------postAudioTranscription end----------")
//            }
//        }
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
                                PodcastDetailScreen(
                                    podcastId = backStackEntry.arguments?.getString("id")!!,
                                )
                            }

                            composable(
                                Destination.lyrics,
                                arguments = listOf(
                                    navArgument("url") {
                                        type = NavType.StringType
                                        defaultValue = "Default url"
                                    },
                                    navArgument("fileName") {
                                        type = NavType.StringType
                                        defaultValue = "Default fileName"
                                    }
                                )
                            ) { backStackEntry ->
                                val url = backStackEntry.arguments?.getString("url", "Default url")!!
                                val fileName = backStackEntry.arguments?.getString("fileName", "Default fileName")!!
                                PodcastLyricsScreen(url, fileName)
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