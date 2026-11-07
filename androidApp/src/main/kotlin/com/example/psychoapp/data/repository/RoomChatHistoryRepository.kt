package com.example.psychoapp.data.repository

import android.util.Log
import com.example.psychoapp.data.local.chat.ChatMessageDao
import com.example.psychoapp.data.local.chat.ChatMessageEntity
import com.example.psychoapp.shared.chat.ChatHistoryRepository
import com.example.psychoapp.shared.chat.ChatMessageUi
import com.example.psychoapp.shared.chat.ChatStatus
import com.example.psychoapp.shared.model.ChatMessage
import com.example.psychoapp.shared.model.ChatRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "RoomChatHistoryRepo"

class RoomChatHistoryRepository(private val dao: ChatMessageDao) : ChatHistoryRepository {
    override fun observeByUser(userLogin: String): Flow<List<ChatMessageUi>> =
        dao.observeByUser(userLogin).map { entities ->
        entities.mapNotNull { it.toUiOrNull() }
    }

    override suspend fun getHistoryForApi(userLogin: String, limit: Int): List<ChatMessage> =
        dao.getHistoryByUser(userLogin, limit).mapNotNull{ it.toDtoOrNull() }

    override suspend fun saveMessage(
        userLogin: String,
        role: ChatRole,
        content: String,
        status: ChatStatus
    ): Long {
        val entity = ChatMessageEntity(
            userLogin = userLogin,
            role = role.name,
            content = content,
            timestamp = System.currentTimeMillis(),
            status = status.name
        )

        return dao.insert(entity)
    }

    override suspend fun updateMessage(
        id: Long,
        content: String,
        status: ChatStatus,
    ) {
        dao.updateContentAndStatus(id = id, content = content, status = status.name)
    }

    override suspend fun clearHistory(userLogin: String) {
        dao.clearByUser(userLogin)
    }

    override suspend fun deleteMessage(userLogin: String, id: Long) {
        dao.deleteMessage(userLogin, id)
    }

    private fun ChatMessageEntity.toUiOrNull(): ChatMessageUi? {
        val chatRole = role.toChatRoleOrNull()
        if (chatRole == null) {
            Log.w(TAG, "Unknown role '$role' for message id=$id, skipping")
            return null
        }
        val chatStatus = status.toChatStatusOrNull()
        if (chatStatus == null) {
            Log.w(TAG, "Unknown status '$status' for message id=$id, defaulting to DONE")
        }
        return ChatMessageUi(
            id = id,
            role = chatRole,
            content = content,
            timestamp = timestamp,
            status = chatStatus ?: ChatStatus.DONE,
        )
    }

    private fun ChatMessageEntity.toDtoOrNull(): ChatMessage? {
        val chatRole = role.toChatRoleOrNull()
        if (chatRole == null) {
            Log.w(TAG, "Unknown role '$role' for message id=$id, skipping in API history")
            return null
        }
        return ChatMessage(role = chatRole, content = content)
    }

    private fun String.toChatRoleOrNull(): ChatRole? =
        ChatRole.entries.find { it.name == this }

    private fun String.toChatStatusOrNull(): ChatStatus? =
        ChatStatus.entries.find { it.name == this }
}