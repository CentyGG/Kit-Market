package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.OrderRepository

class CancelOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: Long): Boolean = repository.cancelOrder(orderId)
}
