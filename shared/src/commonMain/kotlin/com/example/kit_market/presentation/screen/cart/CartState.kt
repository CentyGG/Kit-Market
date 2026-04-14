package com.example.kit_market.presentation.screen.cart

import com.example.kit_market.domain.model.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = true
)
