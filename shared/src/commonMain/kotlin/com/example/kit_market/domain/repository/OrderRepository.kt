package com.example.kit_market.domain.repository

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.model.PaymentInfo
import com.example.kit_market.domain.model.PaymentStatus

interface OrderRepository {
    suspend fun createOrder(items: List<CartItem>, paymentType: String = "cash"): Order
    suspend fun getOrders(): List<Order>
    suspend fun getOrderById(id: Long): Order
    suspend fun cancelOrder(orderId: Long): Boolean
    suspend fun createPayment(orderId: Long): PaymentInfo
    suspend fun getPaymentStatus(paymentId: String): PaymentStatus
    suspend fun confirmPayment(paymentId: String): String
}
