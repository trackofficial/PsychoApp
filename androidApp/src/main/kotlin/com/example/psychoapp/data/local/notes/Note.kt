package com.example.psychoapp.data.local.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = Note.TABLE_NAME
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COLUMN)
    val id: Long = 0,
    @ColumnInfo(name = TITLE_COLUMN)
    val title: String,
    @ColumnInfo(name = TEXT_COLUMN)
    val body: String,
    @ColumnInfo(name = TIMESTAMP_COLUMN)
    val timestamp: Instant = Instant.now(),
    @ColumnInfo(name = USER_LOGIN)
    val userLogin: String
) {
    companion object {
        const val TABLE_NAME = "notes"

        const val ID_COLUMN = "id"
        const val TITLE_COLUMN = "title"
        const val TEXT_COLUMN = "body"
        const val TIMESTAMP_COLUMN = "timestamp"
        const val USER_LOGIN = "user_login"
    }
}
