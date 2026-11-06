// * один чанк из SSE-потока (при stream = true) (ChatResponse - "готовый ответ", ChatStreamChunk - "одно слово из этого ответа")

package com.example.psychoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatStreamChunk (
    val id: String,
    val model: String,
    val choices: List<ChatStreamChoice>,
    val usage: ChatUsage? = null
)

@Serializable
data class ChatStreamChoice (
    val index: Int = 0,
    val delta: ChatStreamDelta,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class ChatStreamDelta (
    val role: ChatRole? = null,
    val content: String? = null
)