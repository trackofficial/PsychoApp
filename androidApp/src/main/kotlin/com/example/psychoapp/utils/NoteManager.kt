package com.example.psychoapp.utils

import com.example.psychoapp.data.local.notes.Note
import com.example.psychoapp.data.local.notes.NotesDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteManager(private val notesDB: NotesDb) {
    private val notesDao = notesDB.getNotesDao()

    // * добавление заметки в бд
    suspend fun addNote(note: Note) : Boolean = withContext(Dispatchers.IO) {
        val resId = notesDao.insert(note)

        return@withContext resId > 0
    }

    // * редактирование заметки
    suspend fun updateNote(note: Note) : Boolean = withContext(Dispatchers.IO) {
        val editedNote = notesDao.update(note)

        return@withContext editedNote > 0
    }
}