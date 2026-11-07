// * DTO(Data Transfer Object — объект передачи данных) сообщения чата

package com.example.psychoapp.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ChatRole{
    @SerialName("system")
    SYSTEM,

    @SerialName("user")
    USER,

    @SerialName("assistant")
    ASSISTANT,
}

@Serializable
data class ChatMessage (
    val role: ChatRole,
    val content: String
)