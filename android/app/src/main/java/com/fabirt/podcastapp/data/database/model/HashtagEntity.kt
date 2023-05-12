package com.fabirt.podcastapp.data.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Created by dion on 2023/05/10.
 */
@Entity(tableName = "Hashtag")
data class HashtagEntity(
    @PrimaryKey(autoGenerate = true)
    val hashtagId: Long? = null,
    val name: String,
)

@Entity(primaryKeys = ["guid", "hashtagId"])
data class ParagraphsHashtagCrossRef(
    val guid: String,
    val hashtagId: String,
)

@Entity(primaryKeys = ["guid", "hashtagId"])
data class ArticleHashtagCrossRef(
    val guid: String,
    val hashtagId: Int,
)

data class HashtagWithArticles(
    @Embedded val hashtag: HashtagEntity,
    @Relation(
        parentColumn = "hashtagId",
        entityColumn = "guid",
        associateBy = Junction(
            ArticleHashtagCrossRef::class,
            parentColumn = "hashtagId",
            entityColumn = "guid"
        )
    )
    val articles: List<ArticleEntity>,
)