package com.example.psychoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class AddNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_note_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: "unknown_user"

        val noteTitle = findViewById<EditText>(R.id.edit_note_title)
        val noteText = findViewById<EditText>(R.id.edit_note_text)

        val saveNoteBtn = findViewById<Button>(R.id.btn_save_note)
        saveNoteBtn.setOnClickListener {
            val title = noteTitle.text.toString().trim()
            val text = noteText.text.toString().trim()

            if (title == "" || text == "") {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else {
                val note = Note(title=title, body=text, userLogin=userLogin)
                val db = DbHelper(this, null)

                db.addNote(note)

                finish()
            }
        }
    }
}