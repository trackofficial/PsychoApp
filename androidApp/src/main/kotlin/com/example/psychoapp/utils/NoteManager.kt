package com.example.psychoapp.utils

import com.example.psychoapp.data.local.AppDb
import com.example.psychoapp.data.local.notes.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteManager(db: AppDb) {
    private val notesDao = db.notesDao()

    // * добавление заметки в бд
    suspend fun addNote(note: Note): Boolean = withContext(Dispatchers.IO) {
        val resId = notesDao.insert(note)

        return@withContext resId > 0
    }

    // * редактирование заметки
    suspend fun updateNote(note: Note): Boolean = withContext(Dispatchers.IO) {
        val editedNote = notesDao.update(note)

        return@withContext editedNote > 0
    }

    suspend fun deleteNote(note: Note): Boolean = withContext(Dispatchers.IO) {
        val deletedNote = notesDao.delete(note)

        return@withContext deletedNote > 0
    }
}