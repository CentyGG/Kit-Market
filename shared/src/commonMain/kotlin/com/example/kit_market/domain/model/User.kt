package com.example.kit_market.domain.model

data class User(
    val id: Long = 0,
    val phone: String,
    val firstName: String,
    val lastName: String
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
