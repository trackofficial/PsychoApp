package com.example.psychoapp

import java.security.MessageDigest

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.Instant

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "PsychoApp", factory, 3) {
    override fun onCreate(db: SQLiteDatabase?) {
        val userQuery = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT UNIQUE, password_hash TEXT)"
        db!!.execSQL(userQuery)

        val notesQuery = "CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, body TEXT, timestamp INTEGER NOT NULL, user_login TEXT, FOREIGN KEY (user_login) REFERENCES users (login) ON DELETE CASCADE ON UPDATE CASCADE)"
        db.execSQL(notesQuery)

        val aiQuery = "CREATE TABLE IF NOT EXISTS ai_chat_messages (id INTEGER PRIMARY KEY AUTOINCREMENT, user_login TEXT NOT NULL, role TEXT NOT NULL, content TEXT NOT NULL, timestamp INTEGER NOT NULL, FOREIGN KEY (user_login) REFERENCES users (login) ON DELETE CASCADE ON UPDATE CASCADE)"
        db.execSQL(aiQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val usersQuery = "DROP TABLE IF EXISTS users"
        db!!.execSQL(usersQuery)

        val aiQuery = "DROP TABLE IF EXISTS ai_chat_message"
        db.execSQL(aiQuery)

        onCreate(db) // * создаём бд заново после сброса
    }

    // * хеширование пароля (самый простой способ)
    fun hashPassword(password: String) : String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") { byte -> "%02x".format(byte) }
    }

    // ! ---------- ПОЛЬЗОВАТЕЛИ ----------
    // * проверка, существует ли в бд введённый юзером логин
    fun isLoginExists(login: String) : Boolean {
        val db = this.readableDatabase
        val query = "SELECT id FROM users WHERE login = ?"
        val cursor = db.rawQuery(query, arrayOf(login))

        val loginExists = cursor.count > 0

        cursor.close()
        db.close()

        return loginExists
    }

    fun registerUser(user: User) : Boolean {
        // * проверка логина на уникальность
        if (isLoginExists(user.login)) {
            return false
        }

        val passwordHash = hashPassword(user.password) // * хеширование пароля

        val vals = ContentValues()
        vals.put("login", user.login)
        vals.put("password_hash", passwordHash) // * добавляем в бд не сам пароль, а его хеш

        val db = this.writableDatabase
        val registration = db.insert("users", null, vals)

        db.close()

        return registration != -1L // * вернёт true если регистрация прошла успешно
    }

    fun loginUser(user: User) : Boolean {
        val db = this.readableDatabase
        val password = hashPassword(user.password)

        val query = "SELECT * FROM users WHERE login = ? AND password_hash = ?"
        val cursor = db.rawQuery(query, arrayOf(user.login, password))

        val isUserFound = cursor.count > 0 // * будет true если cursor.count > 0 (значит юзер нашёлся)

        cursor.close()
        db.close()

        return isUserFound
    }

    // ! ---------- ЗАПИСИ ----------
    fun addNote(note: Note) : Boolean {
        val vals = ContentValues()
        vals.put("title", note.title)
        vals.put("body", note.body)
        vals.put("user_login", note.userLogin)
        vals.put("timestamp", note.timestamp.toEpochMilli())

        val db = this.writableDatabase
        val res = db.insert("notes", null, vals)

        db.close()

        return res != -1L
    }

    fun getUserNotes(userLogin: String) : List<Note> {
        val notes = mutableListOf<Note>()

        val db = readableDatabase
        val query = "SELECT id, title, body, timestamp, user_login FROM notes WHERE user_login = ? ORDER BY timestamp DESC"
        val cursor = db.rawQuery(query, arrayOf(userLogin))

        cursor.use { c ->
            while (c.moveToNext()) {
                notes.add(
                    Note(
                        id = c.getLong(0),
                        title = c.getString(1),
                        body = c.getString(2),
                        timestamp = Instant.ofEpochMilli(c.getLong(3)),
                        userLogin = c.getString(4)
                    )
                )
            }

            db.close()

            return notes
        }
    }

    fun updateNote(id: Long, title: String, body: String) : Boolean {
        val vals = ContentValues()
        vals.put("title", title)
        vals.put("body", body)
        vals.put("timestamp", System.currentTimeMillis())

        val db = writableDatabase
        val res = db.update("notes", vals, "id = ?", arrayOf(id.toString()))

        db.close()

        return res.toLong() != -1L
    }

    // ! ---------- ИСТОРИЯ ЧАТА С ИИ ----------
    fun addAiMessage(userLogin: String, role: String, content: String): Boolean {
        val vals = ContentValues().apply {
            put("user_login", userLogin)
            put("role", role) // * user или assistant
            put("content", content)
            put("timestamp", System.currentTimeMillis())
        }
        val db = writableDatabase
        val res = db.insert("ai_chat_messages", null, vals)
        db.close()
        return res != -1L
    }

    fun getAiChatHistory(userLogin: String): List<AiMessage> {
        val list = mutableListOf<AiMessage>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, user_login, role, content, timestamp FROM ai_chat_messages " +
                    "WHERE user_login = ? ORDER BY timestamp ASC",
            arrayOf(userLogin)
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    AiMessage(
                        id = c.getLong(0),
                        userLogin = c.getString(1),
                        role = c.getString(2),
                        content = c.getString(3),
                        timestamp = Instant.ofEpochMilli(c.getLong(4))
                    )
                )
            }
        }
        db.close()
        return list
    }

    fun clearAiChatHistory(userLogin: String): Boolean {
        val db = writableDatabase
        val res = db.delete("ai_chat_messages", "user_login = ?", arrayOf(userLogin))
        db.close()
        return res > 0
    }
}