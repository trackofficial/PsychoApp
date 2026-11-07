package com.example.psychoapp.debug

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.psychoapp.data.local.chat.ChatMessagesDbProvider
import com.example.psychoapp.data.repository.RoomChatHistoryRepository
import com.example.psychoapp.shared.chat.ChatHistoryRepository
import com.example.psychoapp.shared.chat.ChatStatus
import com.example.psychoapp.shared.model.ChatRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

private const val TAG = "ChatHistorySmoke"

suspend fun runChatHistorySmokeTest(
    context: Context,
    userLogin: String = "smoke-test-user",
) {
    Log.d(TAG, "=== Chat history smoke test started ===")
    toast(context, "Room: старт")

    val dao = ChatMessagesDbProvider.get(context).getChatMessagesDao()
    val repository: ChatHistoryRepository = RoomChatHistoryRepository(dao)

    try {
        repository.clearHistory(userLogin)

        val userMsgId = repository.saveMessage(
            userLogin = userLogin,
            role = ChatRole.USER,
            content = "Тестовое сообщение пользователя",
        )
        Log.d(TAG, "Saved user message id=$userMsgId")

        val assistantMsgId = repository.saveMessage(
            userLogin = userLogin,
            role = ChatRole.ASSISTANT,
            content = "",
            status = ChatStatus.STREAMING,
        )
        Log.d(TAG, "Saved assistant placeholder id=$assistantMsgId")

        repository.updateMessage(
            id = assistantMsgId,
            content = "Финальный ответ ассистента",
            status = ChatStatus.DONE,
        )
        Log.d(TAG, "Updated assistant message id=$assistantMsgId")

        val forApi = repository.getHistoryForApi(userLogin, limit = 10)
        Log.d(TAG, "getHistoryForApi returned ${forApi.size} messages:")
        forApi.forEach { m ->
            Log.d(TAG, "  ${m.role}: '${m.content}'")
        }

        val uiList = repository.observeByUser(userLogin).first()
        Log.d(TAG, "observeByUser returned ${uiList.size} messages:")
        uiList.forEach { ui ->
            Log.d(TAG, "  id=${ui.id} ${ui.role} [${ui.status}]: '${ui.content}'")
        }

        toast(
            context,
            "OK: API=${forApi.size}, UI=${uiList.size}\n" +
                    forApi.joinToString("\n") {
                        "${it.role}: ${it.content.take(20)}"
                    }
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Log.e(TAG, "Smoke test failed", e)
        toast(context, "ОШИБКА: ${e.message}")
    }

    Log.d(TAG, "=== Chat history smoke test finished ===")
}

private fun toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}