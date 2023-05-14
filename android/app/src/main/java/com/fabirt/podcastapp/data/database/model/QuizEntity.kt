package com.fabirt.podcastapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by dion on 2023/05/14.
 */

@Entity(tableName = "Quiz")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "article_id")
    val articleId: Int,
    @ColumnInfo(name = "paragraph_id")
    val paragraphId: Int,
    val question: String,
    val options: List<String>,
    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String,
)