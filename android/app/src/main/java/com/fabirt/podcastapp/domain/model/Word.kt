package com.fabirt.podcastapp.domain.model

data class DailyWord(
    val title: String,
    val words: List<Word>
)

data class Word(
    val word: String,
    val translate: String,
    val example: String
)