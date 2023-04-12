package com.fabirt.podcastapp.ui.navigation

object Destination {
    const val welcome = "welcome"
    const val home = "home"
    const val podcast = "podcast/{id}"
    const val lyrics = "lyrics/{id}"

    fun podcast(id: String): String = "podcast/$id"

    fun lyrics(id: String): String = "lyrics/$id"
}