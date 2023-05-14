package com.fabirt.podcastapp.domain.model

import com.fabirt.podcastapp.data.database.model.CaptionEntity

data class Caption(val index: Int, val start: Long, val end: Long, val captionText: String) {
    companion object {
        fun fromEntity(captionsEntity: CaptionEntity) = Caption(
            index = captionsEntity.index,
            start = captionsEntity.start,
            end = captionsEntity.end,
            captionText = captionsEntity.captionText
        )
    }

    fun asEntity(audioId: String) = CaptionEntity(
        articleId = audioId,
        index = index,
        start = start,
        end = end,
        captionText = captionText
    )
}