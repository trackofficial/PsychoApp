// * JSON, который OpenRouter отдаёт целиком (при stream = false)

package com.example.psychoapp.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse (
    val id: String,
    val model: String,
    val choices: List<ChatChoice>,
    val usage: ChatUsage? = null
)

@Serializable
data class ChatChoice (
    val index: Int = 0,
    val message: ChatMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class ChatUsage (
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int
)