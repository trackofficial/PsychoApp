// * контракт между ViewModel и сетевым слоем (кто угодно, кто реализует ChatApi, умеет делать две вещи — отдавать стриминговый ответ и давать цельный ответ)

package com.example.psychoapp.shared.chat

import com.example.psychoapp.shared.model.ChatMessage
import com.example.psychoapp.shared.model.ChatResponse
import com.example.psychoapp.shared.model.ChatStreamChunk
import kotlinx.coroutines.flow.Flow

interface ChatApi {
    fun streamReply (
        model: String,
        messages: List<ChatMessage>
    ): Flow<ChatStreamChunk>

    suspend fun complete (
        model: String,
        messages: List<ChatMessage>
    ): ChatResponse
}