package com.example.psychoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.notes_activity)

        val userLogin = intent.getStringExtra("USER_LOGIN")

        val recycler = findViewById<RecyclerView>(R.id.notes_recycler)
        val emptyView = findViewById<TextView>(R.id.empty_view)
        val btnAdd = findViewById<Button>(R.id.btn_add_note)

        val adapter = NoteAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val userLogin = intent.getStringExtra("USER_LOGIN") ?: "unknown_user"

        val db = DbHelper(this, null)
        val notes = db.getUserNotes(userLogin)

        val recycler = findViewById<RecyclerView>(R.id.notes_recycler)
        val emptyView = findViewById<TextView>(R.id.empty_view)

        (recycler.adapter as NoteAdapter).submitList(notes)
        emptyView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }
}