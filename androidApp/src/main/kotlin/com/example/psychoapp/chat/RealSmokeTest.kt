package com.example.psychoapp.chat

import android.content.Context
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.psychoapp.BuildConfig
import com.example.psychoapp.model.ChatMessage
import com.example.psychoapp.model.ChatRole
import kotlinx.coroutines.CancellationException

private const val TAG = "RealSmokeTest"

suspend fun runRealCompleteSmokeTest(context: Context) {
    Log.d(TAG, "=== Real smoke test started ===")
    toast(context, "Запрос к OpenRouter...")

    val messages = listOf(
        ChatMessage(role = ChatRole.USER, content = "Ответь одним коротким предложением: ты меня слышишь?")
    )

    try {
        val response = ChatApiProvider.api.complete(
            model = ChatApiProvider.DEFAULT_MODEL,
            messages = messages,
        )

        val reply = response.choices.firstOrNull()?.message?.content ?: "<пусто>"
        val usage = response.usage

        Log.d(TAG, "model=${response.model} id=${response.id}")
        Log.d(TAG, "finishReason=${response.choices.firstOrNull()?.finishReason}")
        Log.d(TAG, "usage=${usage?.promptTokens}/${usage?.completionTokens}/${usage?.totalTokens}")
        Log.d(TAG, "reply='$reply'")

        toast(context, "OK: $reply")
    } catch (e: CancellationException) {
        throw e
    } catch (e: ChatException) {
        val reason = when (e) {
            is ChatException.Unauthorized -> "${e.message}"
            is ChatException.RateLimited -> "RateLimited (429)"
            is ChatException.ServerError -> "ServerError ${e.status}"
            is ChatException.Network -> "Network (нет сети или таймаут)"
            is ChatException.InvalidResponse -> "InvalidResponse (битый JSON)"
            is ChatException.Unknown -> "Unknown: ${e.message}"
        }
        Log.e(TAG, "ChatException: $reason", e)
        toast(context, "ОШИБКА: $reason")

    } catch (e: Throwable) {
        Log.e(TAG, "Unexpected error", e)
        toast(context, "НЕОЖИДАННАЯ ОШИБКА: ${e.message}")
    }

    Log.d(TAG, "=== Real smoke test finished ===")
}

private fun toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}