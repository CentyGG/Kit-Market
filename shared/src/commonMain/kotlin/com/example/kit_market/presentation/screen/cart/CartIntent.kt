package com.example.kit_market.presentation.screen.cart

sealed interface CartIntent {
    data class Increment(val productId: Long) : CartIntent
    data class Decrement(val productId: Long) : CartIntent
    data class Remove(val productId: Long) : CartIntent
}
