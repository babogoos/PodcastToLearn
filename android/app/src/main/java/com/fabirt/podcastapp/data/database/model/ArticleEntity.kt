package com.fabirt.podcastapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by dion on 2023/05/10.
 */

@Entity(tableName = "Article")
data class ArticleEntity(
    @PrimaryKey
    val guid: String,
    val date: String,
    val orginArticle: String,
    val orginDescription: String,
)
