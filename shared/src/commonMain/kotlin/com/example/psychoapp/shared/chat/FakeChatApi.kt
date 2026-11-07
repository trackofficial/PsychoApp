// * реализация ChatApi без сети

package com.example.psychoapp.shared.chat

import com.example.psychoapp.shared.model.ChatChoice
import com.example.psychoapp.shared.model.ChatMessage
import com.example.psychoapp.shared.model.ChatResponse
import com.example.psychoapp.shared.model.ChatRole
import com.example.psychoapp.shared.model.ChatStreamChoice
import com.example.psychoapp.shared.model.ChatStreamChunk
import com.example.psychoapp.shared.model.ChatStreamDelta
import com.example.psychoapp.shared.model.ChatUsage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds

class FakeChatApi(
    private val replyText: String = "Привет! Я рядом. Расскажи, что тебя беспокоит?",
    private val chunkDelayMs: Long = 120L
) : ChatApi {
    override fun streamReply(model: String, messages: List<ChatMessage>): Flow<ChatStreamChunk> =
        flow {
            val generationId = "fake-generation"

            emit (
                ChatStreamChunk (
                    id = generationId,
                    model = model,
                    choices = listOf(
                        ChatStreamChoice(
                            index = 0,
                            delta = ChatStreamDelta(role = ChatRole.ASSISTANT),
                            finishReason = null
                        )
                    )
                )
            )

            replyText.chunked(6).forEach { piece ->
                delay(chunkDelayMs.milliseconds)
                emit(
                    ChatStreamChunk(
                        id = generationId,
                        model = model,
                        choices = listOf(
                            ChatStreamChoice(
                                index = 0,
                                delta = ChatStreamDelta(content = piece),
                                finishReason = null
                            )
                        )
                    )
                )
            }

            delay(chunkDelayMs.milliseconds)
            emit(
                ChatStreamChunk(
                    id = generationId,
                    model = model,
                    choices = listOf(
                        ChatStreamChoice(
                            index = 0,
                            delta = ChatStreamDelta(),
                            finishReason = "stop"
                        )
                    )
                )
            )
        }

    override suspend fun complete(model: String, messages: List<ChatMessage>): ChatResponse {
        val promptTokens = messages.sumOf { it.content.length / 4 }
        val completionTokens = replyText.length / 4

        return ChatResponse(
            id = "fake-generation",
            model = model,
            choices = listOf(
                ChatChoice(
                    index = 0,
                    message = ChatMessage(role = ChatRole.ASSISTANT, content = replyText),
                    finishReason = "stop"
                )
            ),
            usage = ChatUsage(
                promptTokens = promptTokens,
                completionTokens = completionTokens,
                totalTokens = promptTokens + completionTokens
            )
        )
    }
}