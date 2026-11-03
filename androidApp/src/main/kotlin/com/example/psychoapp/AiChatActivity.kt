package com.example.psychoapp

import AiService
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_chat_activity)

        val db = DbHelper(this, null)
        val aiService = AiService(apiKey = BuildConfig.AI_API_KEY)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: SessionManager(this).userLogin ?: "unknown_user"

        val buttonSend = findViewById<Button>(R.id.buttonSend)
        val input = findViewById<EditText>(R.id.editTextMessage)

        buttonSend.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty() || userLogin.isEmpty()) return@setOnClickListener
            input.text.clear()
            buttonSend.isEnabled = false

            lifecycleScope.launch {
                // 1) сохраняем запрос пользователя
                withContext(Dispatchers.IO) {
                    db.addAiMessage(userLogin, "user", text)
                }

                // 2) получаем ответ нейросети
                val answer = try {
                    aiService.getAiResponse(text)
                } catch (e: Exception) {
                    "Ошибка: ${e.message}"
                }

                // 3) сохраняем ответ ассистента
                withContext(Dispatchers.IO) {
                    db.addAiMessage(userLogin, "assistant", answer)
                }

                // 4) пока что показываем ответ в Toast (вместо этого — RecyclerView)
                Toast.makeText(this@AiChatActivity, answer, Toast.LENGTH_LONG).show()
                buttonSend.isEnabled = true
            }
        }
    }
}