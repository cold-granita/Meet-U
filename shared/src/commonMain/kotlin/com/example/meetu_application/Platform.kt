package com.example.meetu_application

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform