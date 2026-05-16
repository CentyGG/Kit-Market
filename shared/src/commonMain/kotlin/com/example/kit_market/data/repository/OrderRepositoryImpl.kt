package com.example.kit_market.data.repository

import com.example.kit_market.data.remote.ApiService
import com.example.kit_market.data.remote.dto.OrderItemRequest
import com.example.kit_market.data.remote.dto.OrderResponse
import com.example.kit_market.domain.model.*
import com.example.kit_market.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val apiService: ApiService
) : OrderRepository {

    override suspend fun createOrder(items: List<CartItem>, paymentType: String, pickupTime: String?): Order {
        val request = items.map { OrderItemRequest(it.product.id, it.quantity) }
        return apiService.createOrder(request, paymentType, pickupTime).toDomain()
    }

    override suspend fun getOrders(): List<Order> {
        return apiService.getOrders().map { it.toDomain() }
    }

    override suspend fun getOrderById(id: Long): Order {
        return apiService.getOrderById(id).toDomain()
    }

    override suspend fun cancelOrder(orderId: Long): Boolean {
        return apiService.cancelOrder(orderId)
    }

    override suspend fun createPayment(orderId: Long): PaymentInfo {
        val response = apiService.createPayment(orderId)
        return PaymentInfo(paymentId = response.paymentId, paymentUrl = response.paymentUrl)
    }

    override suspend fun getPaymentStatus(paymentId: String): PaymentStatus {
        val response = apiService.getPaymentStatus(paymentId)
        return PaymentStatus(
            paymentId = response.paymentId,
            status = response.status,
            amount = response.amount
        )
    }

    override suspend fun confirmPayment(paymentId: String): String {
        val response = apiService.confirmPayment(paymentId)
        return response.status
    }

    override suspend fun getWorkerOrders(status: String?): List<Order> {
        return apiService.getWorkerOrders(status).map { it.toDomain() }
    }

    override suspend fun updateOrderStatus(orderId: Long, status: String): Boolean {
        return apiService.updateOrderStatus(orderId, status)
    }

    override suspend fun workerCancelOrder(orderId: Long): Boolean {
        return apiService.workerCancelOrder(orderId)
    }

    private fun OrderResponse.toDomain(): Order = Order(
        id = id,
        userId = userId,
        items = items.map { item ->
            OrderItem(
                id = item.id,
                productId = item.productId,
                productName = item.productName,
                quantity = item.quantity,
                price = item.price.toDouble() / 100.0
            )
        },
        totalPrice = total.toDouble() / 100.0,
        date = orderDate,
        status = OrderStatus.fromString(status),
        paymentType = paymentType,
        hasReceipt = hasReceipt,
        pickupTime = pickupTime
    )
}
