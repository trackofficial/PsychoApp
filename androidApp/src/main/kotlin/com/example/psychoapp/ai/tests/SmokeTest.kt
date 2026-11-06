package com.example.psychoapp.ai.tests

import android.util.Log
import com.example.psychoapp.chat.FakeChatApi
import com.example.psychoapp.model.ChatMessage
import com.example.psychoapp.model.ChatRole
import kotlinx.coroutines.flow.catch

private const val TAG = "SmokeTest"

suspend fun runFakeStreamSmokeTest() {
    Log.d(TAG, "=== Smoke test started ===")

    val fakeApi = FakeChatApi(chunkDelayMs = 200L)

    val collected = StringBuilder()

    fakeApi
        .streamReply(
            model = "fake-model",
            messages = listOf(
                ChatMessage(role = ChatRole.USER, content = "привет")
            )
        )
        .catch { e->
            Log.e(TAG, "Stream failed", e)
        }
        .collect { chunk ->
            val choice = chunk.choices.firstOrNull()
            val delta = choice?.delta
            val piece = delta?.content

            Log.d(
                TAG,
                "chunk: id=${chunk.id}, role=${delta?.role}, content='${piece ?: ""}', finishReason=${choice?.finishReason}"
            )

            if (piece != null) {
                collected.append(piece)
            }
        }
    Log.d(TAG, "=== Collected text: '$collected' ===")
    Log.d(TAG, "=== Smoke test finished ===")
}