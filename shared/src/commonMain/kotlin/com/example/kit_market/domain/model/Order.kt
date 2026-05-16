package com.example.kit_market.domain.model

data class Order(
    val id: Long,
    val userId: Long = 0,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val date: String,
    val status: OrderStatus,
    val paymentType: String = "cash",
    val hasReceipt: Boolean = false,
    val pickupTime: String? = null
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
    ASSEMBLING,
    READY,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(value: String): OrderStatus = when (value.lowercase()) {
            "created" -> CREATED
            "paid" -> PAID
            "assembling" -> ASSEMBLING
            "ready" -> READY
            "completed" -> COMPLETED
            "cancelled" -> CANCELLED
            else -> CREATED
        }
    }

    fun toRussian(): String = when (this) {
        CREATED -> "Создан"
        PAID -> "Оплачен"
        ASSEMBLING -> "В сборке"
        READY -> "Готов к выдаче"
        COMPLETED -> "Выдан"
        CANCELLED -> "Отменён"
    }
}
