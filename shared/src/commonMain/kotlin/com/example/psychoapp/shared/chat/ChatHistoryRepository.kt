// * контракт между ViewModel и слоем хранения

package com.example.psychoapp.shared.chat

import com.example.psychoapp.shared.model.ChatMessage
import com.example.psychoapp.shared.model.ChatRole
import kotlinx.coroutines.flow.Flow

enum class ChatStatus {
    STREAMING,
    DONE,
    ERROR
}

data class ChatMessageUi(
    val id: Long,
    val role: ChatRole,
    val content: String,
    val timestamp: Long,
    val status: ChatStatus
)

interface ChatHistoryRepository {
    fun observeByUser(userLogin: String): Flow<List<ChatMessageUi>>

    suspend fun getHistoryForApi(
        userLogin: String,
        limit: Int = 50
    ): List<ChatMessage>

    suspend fun saveMessage(
        userLogin: String,
        role: ChatRole,
        content: String,
        status: ChatStatus = ChatStatus.DONE,
    ): Long

    suspend fun updateMessage(
        id: Long,
        content: String,
        status: ChatStatus,
    )

    suspend fun clearHistory(userLogin: String)

    suspend fun deleteMessage(userLogin: String, id: Long)
}