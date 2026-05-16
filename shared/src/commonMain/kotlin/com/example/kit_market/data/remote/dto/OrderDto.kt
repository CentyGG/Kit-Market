package com.example.kit_market.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(val productId: Long, val quantity: Int)

@Serializable
data class CreateOrderRequest(val items: List<OrderItemRequest>, val paymentType: String = "cash", val pickupTime: String? = null)

@Serializable
data class OrderResponse(
    val id: Long,
    val userId: Long,
    val orderDate: String,
    val status: String,
    val total: Long,
    val paymentType: String = "cash",
    val items: List<OrderItemResponse>,
    val hasReceipt: Boolean = false,
    val pickupTime: String? = null
)

@Serializable
data class UpdateStatusRequest(val status: String)

@Serializable
data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Long
)
