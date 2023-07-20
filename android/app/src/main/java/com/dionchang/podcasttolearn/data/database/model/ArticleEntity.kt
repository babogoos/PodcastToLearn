package com.dionchang.podcasttolearn.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Created by dion on 2023/05/10.
 */

@Entity(tableName = "Article")
data class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "article_id")
    val articleId: String,
    @ColumnInfo(name = "pub_date_ms")
    val pubDateMS: Long,
    @ColumnInfo(name = "orgin_description")
    val orginDescription: String,
    @ColumnInfo(name = "orgin_article")
    val orginArticle: String? = null,
)

@Entity(primaryKeys = ["article_id", "hashtag_id"])
data class ArticleHashtagCrossRef(
    @ColumnInfo(name = "article_id")
    val articleId: String,
    @ColumnInfo(name = "hashtag_id")
    val hashtagId: Int,
)

// Find all articles with a given hashtag
data class HashtagWithArticles(
    @Embedded val hashtag: HashtagEntity,
    @Relation(
        parentColumn = "hashtag_id",
        entityColumn = "article_id",
        associateBy = Junction(
            ArticleHashtagCrossRef::class,
            parentColumn = "hashtag_id",
            entityColumn = "article_id"
        )
    )
    val articles: List<ArticleEntity>,
)

// Find all hashtags for a given article
data class ArticleWithHashtags(
    @Embedded val article: ArticleEntity,
    @Relation(
        parentColumn = "article_id",
        entityColumn = "hashtag_id",
        associateBy = Junction(
            ArticleHashtagCrossRef::class,
            parentColumn = "article_id",
            entityColumn = "hashtag_id"
        )
    )
    val hashtags: List<HashtagEntity>,
)

