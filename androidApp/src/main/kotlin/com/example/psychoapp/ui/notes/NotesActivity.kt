package com.example.psychoapp.ui.notes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.psychoapp.data.local.notes.NotesDbProvider
import com.example.psychoapp.R
import com.example.psychoapp.utils.SessionManager
import kotlinx.coroutines.launch

class NotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.notes_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: SessionManager(this).userLogin ?: "unknown_user"

        val recycler = findViewById<RecyclerView>(R.id.notes_recycler)
        val btnAdd = findViewById<View>(R.id.btn_add_note)
        val emptyView = findViewById<View>(R.id.empty_view)

        val adapter = NoteAdapter{ note ->
            // * переход на страницу редактирования заметки
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("USER_LOGIN", userLogin)

            intent.putExtra("NOTE_ID", note.id)
            intent.putExtra("NOTE_TITLE", note.title)
            intent.putExtra("NOTE_BODY", note.body)

            startActivity(intent)
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val db = NotesDbProvider.get(this)

        lifecycleScope.launch {
            db.getNotesDao().getUserNotes(userLogin).collect { notes ->
                adapter.submitList(notes)
                emptyView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)
        }
    }
}