package com.fabirt.podcastapp.data.network.model.chat

import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    val id: String,
    @SerializedName("object")
    val obj: String,
    val created: Long,
    val choices: List<ChatCompletionChoice>,
    val usage: ChatCompletionUsage
)

data class ChatCompletionChoice(
    val index: Int,
    val message: ChatMessage,
    val finish_reason: String
)

data class ChatCompletionUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
