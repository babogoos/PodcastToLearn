package com.dionchang.podcasttolearn.domain.model

/**
 * Created by dion on 2023/05/03.
 */
data class OptionsQuiz(
    val question: String,
    val options: List<String>,
    val answer: String,
    val paragraphId: Long,
)
