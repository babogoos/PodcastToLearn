package com.dionchang.podcasttolearn.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dion on 2023/05/14.
 */

data class ParagraphDto(
    val theme: String,
    @SerializedName("paragraph_index")
    val paragraphIndex: Int,
    @SerializedName("paragraph_content")
    val paragraphContent: String,
    val hashtags: List<String>?,
)

data class Options(
    val index: String,
    val value: String
)

data class Quiz(
    val question: String,
    val options: List<Options>,
    val answer: String,
    val reason: String,
    @SerializedName("quiz_hashtags")
    val quizHashtags: List<String>
)