package com.example.kit_market.domain.model

import kotlinx.datetime.LocalDateTime

data class Order(
    val id: Long,
    val items: List<CartItem>,
    val totalPrice: Double,
    val date: LocalDateTime,
    val status: OrderStatus
)

enum class OrderStatus {
    PROCESSING,
    READY,
    DELIVERED
}
