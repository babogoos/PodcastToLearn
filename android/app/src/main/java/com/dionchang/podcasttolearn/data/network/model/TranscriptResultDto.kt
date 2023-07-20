package com.dionchang.podcasttolearn.data.network.model

import com.dionchang.podcasttolearn.domain.model.Caption
import com.dionchang.podcasttolearn.ui.toDate

/**
 * Created by dion on 2023/04/22.
 */
data class TranscriptResultDto(val result: String) {
    fun asDomainModel(): List<Caption> {
        val fullArticleList = mutableListOf<Caption>()
        result.split("\n").chunked(4).forEach { grouped ->
            if (grouped.size < 4) return@forEach
            val index = grouped[0].toInt()
            val start = grouped[1].split(" --> ").first().toDate("HH:mm:ss,SSS").time
            val end = grouped[1].split(" --> ").last().toDate("HH:mm:ss,SSS").time
            val caption = grouped[2]
            fullArticleList.add(Caption(index, start, end, caption))
        }
        return fullArticleList
    }
}