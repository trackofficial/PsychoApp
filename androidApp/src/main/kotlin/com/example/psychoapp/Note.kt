package com.example.psychoapp

import java.time.Instant

data class Note(val id: Long = 0L, val title: String, val body: String, val timestamp: Instant = Instant.now(), val userLogin: String)
