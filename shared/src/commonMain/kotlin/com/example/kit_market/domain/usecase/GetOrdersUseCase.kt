package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.repository.OrderRepository

class GetOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(): List<Order> = repository.getOrders()
}
