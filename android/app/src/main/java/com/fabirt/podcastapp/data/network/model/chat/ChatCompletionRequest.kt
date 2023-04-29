package com.fabirt.podcastapp.data.network.model.chat

data class ChatCompletionRequest(
    val messages: List<ChatMessage>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Float = 0.2f,
)

data class ChatMessage(
    val role: String,
    val content: String
)