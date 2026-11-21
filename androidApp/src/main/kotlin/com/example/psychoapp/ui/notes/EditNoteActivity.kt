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

class EditNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.edit_note_activity)

        val db = AppDbProvider.getDb(this)
        val noteManager = NoteManager(db)

        val userLogin = SessionManager(this).userLogin ?: "unknown_user"

        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: ""
        val noteText = intent.getStringExtra("NOTE_BODY") ?: ""

        val editNoteTitle = findViewById<EditText>(R.id.edit_note_title)
        val editNoteText = findViewById<EditText>(R.id.edit_note_text)

        editNoteTitle.setText(noteTitle)
        editNoteText.setText(noteText)

        val saveNoteBtn = findViewById<Button>(R.id.btn_save_note)
        saveNoteBtn.setOnClickListener {
            var newNoteTitle = editNoteTitle.text.toString().trim()
            var newNoteText = editNoteText.text.toString().trim()

            // * оба пусты
            if (newNoteTitle.isEmpty() && newNoteText.isEmpty()) {
                Toast.makeText(this@EditNoteActivity, "Хотя бы одно поле должно быть заполнено", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // * чем заполнено — тем и дополняем пустое
            if (newNoteTitle.isEmpty()) newNoteTitle = newNoteText
            if (newNoteText.isEmpty()) newNoteText = newNoteTitle

            lifecycleScope.launch {
                val note = Note(
                    id = noteId,
                    title = newNoteTitle,
                    body = newNoteText,
                    userLogin = userLogin,
                    timestamp = System.currentTimeMillis()
                )

                val updatedNote = noteManager.updateNote(note)
                if (updatedNote) {
                    finish()
                } else {
                    Toast.makeText(this@EditNoteActivity, "Ошибка при изменении заметки", Toast.LENGTH_LONG).show()
                }
            }
        }

        val deleteNoteBtn = findViewById<Button>(R.id.btn_delete_note)
        deleteNoteBtn.setOnClickListener {
            val newNoteTitle = editNoteTitle.text.toString().trim()
            val newNoteText = editNoteText.text.toString().trim()

            lifecycleScope.launch {
                val note = Note(
                    id = noteId,
                    title = newNoteTitle,
                    body = newNoteText,
                    userLogin = userLogin,
                    timestamp = System.currentTimeMillis()
                )

                val isDeleted = noteManager.deleteNote(note)
                if (isDeleted) {
                    finish()
                }
                else {
                    Toast.makeText(this@EditNoteActivity, "Ошибка при удалении заметки", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}