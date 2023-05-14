package com.fabirt.podcastapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by dion on 2023/05/14.
 */
@Entity(tableName = "Caption")
data class CaptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "article_id")
    val articleId: String,
    val index: Int,
    val start: Long,
    val end: Long,
    @ColumnInfo(name = "caption_text")
    val captionText: String
)
