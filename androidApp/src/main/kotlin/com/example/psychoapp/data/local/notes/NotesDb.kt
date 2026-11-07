package com.example.psychoapp.data.local.notes

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.psychoapp.data.local.InstantConverter
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [
        Note::class,
    ], version = 1
)
@TypeConverters(InstantConverter::class)
abstract class NotesDb : RoomDatabase() {
    abstract fun getNotesDao(): NotesDao
}

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
    suspend fun delete(note: Note)
}
