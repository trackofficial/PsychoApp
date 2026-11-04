package com.example.psychoapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = Note.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = [User.LOGIN_COLUMN],
            childColumns = [Note.USER_LOGIN]
        )
    ]
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

        private const val ID_COLUMN = "id"
        private const val TITLE_COLUMN = "title"
        private const val TEXT_COLUMN = "body"
        private const val TIMESTAMP_COLUMN = "timestamp"
        private const val USER_LOGIN = "user_login"
    }
}
