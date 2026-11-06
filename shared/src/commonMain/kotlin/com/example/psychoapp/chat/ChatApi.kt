// * контракт между ViewModel и сетевым слоем (кто угодно, кто реализует ChatApi, умеет делать две вещи — отдавать стриминговый ответ и давать цельный ответ)

package com.example.psychoapp.chat

import com.example.psychoapp.model.ChatMessage
import com.example.psychoapp.model.ChatResponse
import com.example.psychoapp.model.ChatStreamChunk
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