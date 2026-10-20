package com.example.psychoapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform