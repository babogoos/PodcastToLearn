package com.fabirt.podcastapp.ui.navigation

object Destination {
    const val welcome = "welcome"
    const val home = "home"
    const val podcast = "podcast/{id}"
    const val lyrics = "lyrics?fileName={fileName}&url={url}"

    fun podcast(id: String): String = "podcast/$id"

    fun lyrics(fileName: String, url: String): String = "lyrics?fileName=$fileName&url=$url"
}