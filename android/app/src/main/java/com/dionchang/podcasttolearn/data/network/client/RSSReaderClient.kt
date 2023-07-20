package com.dionchang.podcasttolearn.data.network.client

import android.content.Context
import androidx.core.text.HtmlCompat
import com.dionchang.podcasttolearn.data.network.model.EpisodeDto
import com.dionchang.podcasttolearn.data.network.model.PodcastDto
import com.dionchang.podcasttolearn.data.network.model.PodcastSearchDto
import com.dionchang.podcasttolearn.ui.toDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tw.ktrssreader.Reader
import tw.ktrssreader.kotlin.model.channel.ITunesChannel
import tw.ktrssreader.kotlin.model.item.ITunesItem


/**
 * Created by dion on 2023/04/05.
 */
class RSSReaderClient(
    @ApplicationContext val context: Context,
) {
    suspend fun fecthRssPodcast(rssSource: String): PodcastSearchDto {
        return withContext(Dispatchers.IO) {
            val iTChannel = Reader.coRead<ITunesChannel>(url = rssSource, config = {
                this.useCache = false
                this.flushCache = true
            })
            val results = iTChannel.items?.take(14)?.map { iTItemData ->
                EpisodeDto(
                    id = iTItemData.guid?.value ?: "",
                    link = iTItemData.link ?: "",
                    audio = iTItemData.enclosure?.url ?: "",
                    image = iTItemData.image ?: "",
                    podcast = PodcastDto(
                        id = iTItemData.episode.toString(),
                        image = iTItemData.image ?: "",
                        thumbnail = iTItemData.image ?: "",
                        listennotesURL = "",
                        titleOriginal = iTItemData.title ?: "",
                        publisherOriginal = iTItemData.author ?: "",
                    ),
                    thumbnail = iTItemData.image ?: "",
                    pubDateMS = iTItemData.pubDate?.toDate()?.time ?: 0,
                    titleOriginal = iTItemData.title ?: "",
                    listennotesURL = iTItemData.link ?: "",
                    audioLengthSec = parseDuration(iTItemData),
                    explicitContent = iTItemData.explicit ?: false,
                    descriptionOriginal = decodeDescription(iTItemData.description ?: ""),
                )
            } ?: emptyList()

            return@withContext PodcastSearchDto(
                count = iTChannel.items?.size?.toLong() ?: 0,
                total = iTChannel.items?.size?.toLong() ?: 0,
                results = results
            )
        }
    }

    private fun parseDuration(iTItemData: ITunesItem): Long {
        return when(iTItemData.author) {
            "Taiwan Public Television Service" -> iTItemData.duration?.toDate("HH:mm:ss")?.time ?: 0
            "TechCrunch" -> iTItemData.duration?.toLong() ?: 0
            else -> 0
        }
    }

    // Todo: This function is only for tech crunch feeds, this should be moved to domain layer.
    private fun decodeDescription(descriptionOrigin: String): String {
        val descriptionDecode = HtmlCompat.fromHtml(
            descriptionOrigin,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toString().trim()
        val descriptionResult = StringBuilder()
        descriptionDecode.split("; ").withIndex().forEach { (index, singleNews) ->
            descriptionResult.append("${index + 1}. $singleNews.\n")
        }
        return descriptionResult.toString()
    }
}