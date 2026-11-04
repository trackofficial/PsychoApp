package com.example.psychoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_note_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: SessionManager(this).userLogin ?: "unknown_user"

        val db = NotesDbProvider.get(this)

        val noteTitle = findViewById<EditText>(R.id.edit_note_title)
        val noteText = findViewById<EditText>(R.id.edit_note_text)

        val saveNoteBtn = findViewById<Button>(R.id.btn_save_note)
        saveNoteBtn.setOnClickListener {
            val title = noteTitle.text.toString().trim()
            val text = noteText.text.toString().trim()

            if (title.isEmpty() || text.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
            }
            else {
                val note = Note(title=title, body=text, userLogin=userLogin)

                lifecycleScope.launch {
                    val insertedNoteId = withContext(Dispatchers.IO) {
                        db.getNotesDao().insert(note)
                    }

                    if (insertedNoteId >= 0L) {
                        Toast.makeText(this@AddNoteActivity, "Заметка успешно сохранена", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else {
                        Toast.makeText(this@AddNoteActivity, "Ошибка при сохранении заметки", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}