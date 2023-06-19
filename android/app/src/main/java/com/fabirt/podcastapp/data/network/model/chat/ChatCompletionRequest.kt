package com.fabirt.podcastapp.data.network.model.chat

data class ChatCompletionRequest(
    val messages: List<ChatMessage>,
    val model: String = "gpt-3.5-turbo-16k-0613",
    val temperature: Float = 0f,
)

data class ChatMessage(
    val role: String,
    val content: String
)