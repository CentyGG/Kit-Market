package com.example.kit_market.presentation.screen.orders

import com.example.kit_market.domain.model.Order

data class OrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
