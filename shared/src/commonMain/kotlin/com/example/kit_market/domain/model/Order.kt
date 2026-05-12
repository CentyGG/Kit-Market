package com.example.kit_market.domain.model

data class Order(
    val id: Long,
    val userId: Long = 0,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val date: String,
    val status: OrderStatus,
    val hasReceipt: Boolean = false
)

data class OrderItem(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    CREATED,
    PAID,
    READY,
    COMPLETED;

    companion object {
        fun fromString(value: String): OrderStatus = when (value.lowercase()) {
            "created" -> CREATED
            "paid" -> PAID
            "ready" -> READY
            "completed" -> COMPLETED
            else -> CREATED
        }
    }

    fun toRussian(): String = when (this) {
        CREATED -> "Создан"
        PAID -> "Оплачен"
        READY -> "Готов к выдаче"
        COMPLETED -> "Выдан"
    }
}
