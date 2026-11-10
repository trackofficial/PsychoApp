package com.example.psychoapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.psychoapp.ui.chat.ChatActivity
import com.example.psychoapp.ui.notes.NotesActivity
import com.example.psychoapp.R
import com.example.psychoapp.debug.runRealCompleteSmokeTest
import com.example.psychoapp.utils.SessionManager
import com.example.psychoapp.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: SessionManager(this).userLogin ?: "unknown_user"

        val toNotes = findViewById<LinearLayout>(R.id.yournotesbutton)
        toNotes.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)
        }

        val toAiAssistant = findViewById<LinearLayout>(R.id.youraiassisbutton)
        toAiAssistant.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)

//            lifecycleScope.launch {
//                // * для проверки работы ИИ -> runRealCompleteSmokeTest(this@MainActivity)
//                runRealCompleteSmokeTest(this@MainActivity)
//                // * для проверки работы БД -> runChatHistorySmokeTest(this@MainActivity)
//            }
        }

        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            SessionManager(this).clear()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

            finish()
        }
    }
}