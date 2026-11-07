package com.example.psychoapp.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.psychoapp.data.local.notes.Note
import com.example.psychoapp.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NoteAdapter(private val onNoteClick: (Note) -> Unit) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(Diff) {
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById<TextView>(R.id.note_title)
        val body: TextView = view.findViewById<TextView>(R.id.note_body)
        val date: TextView = view.findViewById<TextView>(R.id.note_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)

        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.title.text = note.title
        holder.body.text = note.body
        holder.date.text = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault()).format(note.timestamp)

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }
    }

    object Diff : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(a: Note, b: Note) = a.id == b.id
        override fun areContentsTheSame(a: Note, b: Note) = a == b
    }
}