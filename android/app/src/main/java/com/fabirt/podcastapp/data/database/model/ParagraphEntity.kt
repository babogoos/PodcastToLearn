package com.fabirt.podcastapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Created by dion on 2023/05/10.
 */

@Entity(tableName = "Paragraph")
data class ParagraphEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "paragraph_id")
    val paragraphId: Long? = null,
    @ColumnInfo(name = "article_id")
    val articleId: String,
    @ColumnInfo(name = "caption_id")
    val captionId: Long?,
    val index: Int,
    val theme: String = "Misc.",
    val content: String,
)

data class ArticleWithParagraphs(
    @Embedded val article: ArticleEntity,
    @Relation(
        parentColumn = "article_id",
        entityColumn = "article_id"
    )
    val paragraphs: List<ParagraphEntity>,
)

@Entity(primaryKeys = ["paragraph_id", "hashtag_id"])
data class ParagraphsHashtagCrossRef(
    @ColumnInfo(name = "paragraph_id")
    val paragraphId: Long,
    @ColumnInfo(name = "hashtag_id")
    val hashtagId: Long,
)

// Find all hashtags for a given paragraph
data class ParagraphWithHashtags(
    @Embedded val paragraph: ParagraphEntity,
    @Relation(
        parentColumn = "paragraph_id",
        entityColumn = "hashtag_id",
        associateBy = Junction(
            ParagraphsHashtagCrossRef::class,
            parentColumn = "paragraph_id",
            entityColumn = "hashtag_id"
        )
    )
    val hashtags: List<HashtagEntity>,
)
