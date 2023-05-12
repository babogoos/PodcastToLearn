package com.fabirt.podcastapp.data.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Created by dion on 2023/05/10.
 */

@Entity(tableName = "Paragraph", primaryKeys = ["guid", "paragraph_index"])
data class ParagraphEntity(
    val guid: String,
    val paragraph_index: Int,
    val theme: String = "Misc.",
    val content: String,
)

data class ArticleWithParagraphs(
    @Embedded val article: ArticleEntity,
    @Relation(
        parentColumn = "guid",
        entityColumn = "guid"
    )
    val paragraphs: List<ParagraphEntity>,
)