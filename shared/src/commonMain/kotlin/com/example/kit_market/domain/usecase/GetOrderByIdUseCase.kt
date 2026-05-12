package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.repository.OrderRepository

class GetOrderByIdUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(id: Long): Order = repository.getOrderById(id)
}
