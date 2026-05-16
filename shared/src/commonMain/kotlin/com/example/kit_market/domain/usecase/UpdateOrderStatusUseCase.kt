package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.OrderRepository

class UpdateOrderStatusUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: Long, status: String): Boolean =
        repository.updateOrderStatus(orderId, status)
}
