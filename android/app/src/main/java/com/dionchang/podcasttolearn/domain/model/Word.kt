package com.dionchang.podcasttolearn.domain.model

data class DailyWord(
    val audioId: String,
    val words: List<Word>
)

data class Word(
    val word: String,
    val translate: String,
    val example: String,
    val exampleTranslate: String,
)