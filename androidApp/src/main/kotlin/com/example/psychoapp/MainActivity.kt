package com.example.psychoapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN")

        val toNotes = findViewById<TextView>(R.id.redirect_to_notes)
        toNotes.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)
        }
    }
}