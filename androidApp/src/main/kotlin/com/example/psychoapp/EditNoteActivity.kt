package com.example.psychoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class EditNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_note_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: "unknown_user"

        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: ""
        val noteText = intent.getStringExtra("NOTE_BODY") ?: ""

        val editNoteTitle = findViewById<EditText>(R.id.edit_note_title)
        val editNoteText = findViewById<EditText>(R.id.edit_note_text)

        editNoteTitle.setText(noteTitle)
        editNoteText.setText(noteText)

        val saveNoteBtn = findViewById<Button>(R.id.btn_save_note)
        saveNoteBtn.setOnClickListener {
            val newNoteTitle = editNoteTitle.text.toString().trim()
            val newNoteText = editNoteText.text.toString().trim()

            if (newNoteTitle.isEmpty() || newNoteText.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else {
                val db = DbHelper(this, null)

                db.updateNote(noteId, newNoteTitle, newNoteText)

                finish()
            }
        }
    }
}