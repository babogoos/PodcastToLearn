package com.dionchang.podcasttolearn.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dionchang.podcasttolearn.domain.model.Caption
import com.dionchang.podcasttolearn.domain.model.DailyWord
import com.dionchang.podcasttolearn.domain.model.PodcastCaptions
import com.dionchang.podcasttolearn.domain.model.PodcastSearch
import com.dionchang.podcasttolearn.domain.model.Word
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.Instant

class PodcastDataStore(
    private val context: Context
) {
    private val lastAPIFetchMillis = longPreferencesKey("last_api_fetch_millis")
    private val podcastSearchResult = stringPreferencesKey("podcast_search_result")

    companion object {
        private const val TAG = "PodcastDataStore"
    }

    suspend fun storePodcastSearchResult(data: PodcastSearch) {
        context.podcastDataStore.edit { preferences ->
            val jsonString = Gson().toJson(data)
            Log.i(TAG, jsonString)
            preferences[lastAPIFetchMillis] = Instant.now().toEpochMilli()
            preferences[podcastSearchResult] = jsonString
        }
    }

    suspend fun storeTranscriptResult(podcastCaptions: PodcastCaptions) {
        context.podcastDataStore.edit { preferences ->
            preferences[stringPreferencesKey(podcastCaptions.audioId)] = Gson().toJson(podcastCaptions.captions)
        }
    }

    suspend fun readTranscriptResult(audioId: String): PodcastCaptions? {
        return context.podcastDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(audioId)]?.let {
                val captions = Gson().fromJson<List<Caption>>(it, object : TypeToken<List<Caption>>() {}.type)
                PodcastCaptions(audioId, captions)
            }
        }.firstOrNull()
    }

    suspend fun readLastPodcastSearchResult(): PodcastSearch {
        return context.podcastDataStore.data.map { preferences ->
            val jsonString = preferences[podcastSearchResult]
            Gson().fromJson(jsonString, PodcastSearch::class.java)
        }.first()
    }

    suspend fun canFetchAPI(): Boolean {
        return context.podcastDataStore.data.map { preferences ->
            val epochMillis = preferences[lastAPIFetchMillis]

            return@map if (epochMillis != null) {
                val minDiffMillis = 36 * 60 * 60 * 1000L
                val now = Instant.now().toEpochMilli()
                (now - minDiffMillis) > epochMillis
            } else {
                true
            }
        }.first()
    }

    suspend fun storeDailyWordResult(dailyWord: DailyWord) {
        context.podcastDataStore.edit { preferences ->
            preferences[stringPreferencesKey("Words_${dailyWord.audioId}")] = Gson().toJson(dailyWord.words)
        }
    }

    suspend fun readDailyWordResult(audioId: String): DailyWord? {
        return context.podcastDataStore.data.map { preferences ->
            preferences[stringPreferencesKey("Words_$audioId")]?.let {
                val words = Gson().fromJson<List<Word>>(it, object : TypeToken<List<Word>>() {}.type)
                DailyWord(audioId, words)
            }
        }.firstOrNull()
    }
}

private val Context.podcastDataStore: DataStore<Preferences> by preferencesDataStore(name = "podcasts")
