package com.example.psychoapp.ui.notes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.psychoapp.data.local.notes.model.Note
import com.example.psychoapp.utils.NoteManager
import com.example.psychoapp.R
import com.example.psychoapp.data.local.AppDbProvider
import com.example.psychoapp.utils.SessionManager
import kotlinx.coroutines.launch

class AddNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_note_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: SessionManager(this).userLogin ?: "unknown_user"

        val db = AppDbProvider.getDb(this)
        val noteManager = NoteManager(db)

        val noteTitle = findViewById<EditText>(R.id.edit_note_title)
        val noteText = findViewById<EditText>(R.id.edit_note_text)

        val saveNoteBtn = findViewById<Button>(R.id.btn_save_note)
        saveNoteBtn.setOnClickListener {
            var title = noteTitle.text.toString().trim()
            var text = noteText.text.toString().trim()

            // * оба пусты
            if (title.isEmpty() && text.isEmpty()) {
                Toast.makeText(this@AddNoteActivity, "Хотя бы одно поле должно быть заполнено", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // * чем заполнено — тем и дополняем пустое
            if (title.isEmpty()) title = text
            if (text.isEmpty()) text = title

            lifecycleScope.launch {
                val note = Note(
                    title = title,
                    body = text,
                    userLogin = userLogin,
                    timestamp = System.currentTimeMillis()
                )
                val insertedNoteId = noteManager.addNote(note)

                if (insertedNoteId) {
                    finish()
                } else {
                    Toast.makeText(this@AddNoteActivity, "Ошибка при сохранении заметки", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}