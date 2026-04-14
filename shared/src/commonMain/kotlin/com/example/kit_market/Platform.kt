package com.example.kit_market

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform