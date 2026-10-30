package com.example.psychoapp

import java.time.Instant

data class Note(val title: String, val body: String, val timestamp: Instant = Instant.now(), val userLogin: String)
