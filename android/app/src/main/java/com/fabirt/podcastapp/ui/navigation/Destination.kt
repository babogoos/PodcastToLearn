package com.fabirt.podcastapp.ui.navigation

import com.fabirt.podcastapp.ui.viewmodel.DailyWordViewModel

object Destination {
    const val welcome = "welcome"
    const val home = "home"
    const val podcast = "podcast/{id}"
    const val captions = "captions?title={title}&url={url}&audioId={audioId}"
    const val dailyWord = "dailyWord?audioId={audioId}&article={article}"

    fun podcast(id: String): String = "podcast/$id"

    fun captions(title: String, url: String, audioId: String): String = "captions?title=$title&url=$url&audioId=$audioId"

    fun dailyWord(audioId: String, articleVaule: String): String =
        "dailyWord?${DailyWordViewModel.KEY_AUDIO_ID}=$audioId&${DailyWordViewModel.KEY_ARTICLE}=$articleVaule"
}