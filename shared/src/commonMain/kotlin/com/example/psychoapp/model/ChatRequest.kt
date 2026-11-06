// * тело запроса к OpenRouter

package com.example.psychoapp.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest (
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = true
)