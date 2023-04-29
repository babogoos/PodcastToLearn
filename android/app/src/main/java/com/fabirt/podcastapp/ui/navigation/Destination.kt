package com.fabirt.podcastapp.ui.navigation

import com.fabirt.podcastapp.ui.viewmodel.DailyWordViewModel

object Destination {
    const val welcome = "welcome"
    const val home = "home"
    const val podcast = "podcast/{id}"
    const val lyrics = "lyrics?title={title}&url={url}&audioId={audioId}"
    const val dailyWord = "dailyWord?title={title}&article={article}"

    fun podcast(id: String): String = "podcast/$id"

    fun lyrics(title: String, url: String, audioId: String): String = "lyrics?title=$title&url=$url&audioId=$audioId"

    fun dailyWord(title: String, articleVaule: String): String =
        "dailyWord?${DailyWordViewModel.KEY_TITLE}=$title&${DailyWordViewModel.KEY_ARTICLE}=$articleVaule"
}