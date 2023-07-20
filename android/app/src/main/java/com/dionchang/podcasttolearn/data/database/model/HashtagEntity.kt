package com.dionchang.podcasttolearn.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by dion on 2023/05/10.
 */
@Entity(tableName = "Hashtag")
data class HashtagEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hashtag_id")
    val hashtagId: Long? = null,
    val name: String,
)