package com.example.psychoapp.data.local.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.psychoapp.data.local.notes.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes WHERE user_login = :userLogin")
    fun getUserNotes(userLogin: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNote(id: Long): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: Note): Int

    @Delete
    suspend fun delete(note: Note): Int
}