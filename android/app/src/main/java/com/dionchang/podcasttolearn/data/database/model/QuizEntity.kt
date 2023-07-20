package com.dionchang.podcasttolearn.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dionchang.podcasttolearn.domain.model.OptionsQuiz

/**
 * Created by dion on 2023/05/14.
 */

@Entity(tableName = "Quiz")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "article_id")
    val articleId: String,
    @ColumnInfo(name = "paragraph_id")
    val paragraphId: Long,
    val question: String,
    val options: List<String>,
    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String,
) {
    fun asDomainModel() = OptionsQuiz(
        question = question,
        options = options,
        answer = correctAnswer,
        paragraphId = paragraphId
    )
}