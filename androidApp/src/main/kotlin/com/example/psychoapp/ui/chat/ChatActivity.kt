package com.example.psychoapp.ui.chat

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psychoapp.data.remote.ChatApiProvider
import com.example.psychoapp.data.local.chat.ChatMessagesDbProvider
import com.example.psychoapp.R
import com.example.psychoapp.data.repository.RoomChatHistoryRepository
import com.example.psychoapp.utils.SessionManager
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_DISCLAIMER_SHOWN = "disclaimer_shown"
    }

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            repository = RoomChatHistoryRepository(
                ChatMessagesDbProvider.get(applicationContext).getChatMessagesDao()
            ),
            chatApi = ChatApiProvider.api,
            userLogin = intent.getStringExtra("USER_LOGIN") ?: SessionManager(this).userLogin ?: "unknown_user",
            model = ChatApiProvider.DEFAULT_MODEL,
        )
    }

    private lateinit var adapter: ChatMessageAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessageInput: EditText
    private lateinit var btnSend: View
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        rvMessages = findViewById(R.id.rvMessages)
        etMessageInput = findViewById(R.id.etMessageInput)
        btnSend = findViewById(R.id.btnSend)
        tvError = findViewById(R.id.tvError)

        adapter = ChatMessageAdapter()
        rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvMessages.adapter = adapter

        btnSend.setOnClickListener {
            if (viewModel.uiState.value.isStreaming) {
                viewModel.stopStreaming()
            } else {
                val text = etMessageInput.text.toString()
                if (text.isNotBlank()) {
                    viewModel.sendMessage(text)
                    etMessageInput.setText("")
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val wasAtBottom = isAtBottom()
                    adapter.submitList(state.messages) {
                        if (wasAtBottom && state.messages.isNotEmpty()) {
                            rvMessages.scrollToPosition(state.messages.size - 1)
                        }
                    }


                    if (state.errorMessage != null) {
                        tvError.text = state.errorMessage
                        tvError.visibility = View.VISIBLE
                    } else {
                        tvError.visibility = View.GONE
                    }
                }
            }
        }

        showDisclaimerIfNeeded()
    }

    private fun showDisclaimerIfNeeded() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_DISCLAIMER_SHOWN, false)) return

        AlertDialog.Builder(this)
            .setTitle("Важно знать")
            .setMessage(
                "Это приложение — не психотерапия и не медицинская помощь.\n\n" +
                        "Я — ИИ-ассистент. Я могу поддержать и помочь разобраться в мыслях, " +
                        "но не заменяю живого специалиста.\n\n" +
                        "Если тебе очень тяжело или ты думаешь о том, чтобы навредить себе, " +
                        "пожалуйста, обратись за помощью:\n\n" +
                        "Экстренная психологическая помощь:\n" +
                        "• Беларусь: 8-801-100-16-11 (круглосуточно)\n" +
                        "• Россия: 8-495-989-50-50 (круглосуточно)\n\n" +
                        "Или к близкому человеку, которому доверяешь."
            )
            .setPositiveButton("Понятно") { _, _ ->
                prefs.edit().putBoolean(KEY_DISCLAIMER_SHOWN, true).apply()
            }
            .setCancelable(false)
            .show()
    }

    private fun isAtBottom(): Boolean {
        val lm = rvMessages.layoutManager as? LinearLayoutManager ?: return false
        val lastVisible = lm.findLastCompletelyVisibleItemPosition()
        return lastVisible >= adapter.itemCount - 1
    }
}