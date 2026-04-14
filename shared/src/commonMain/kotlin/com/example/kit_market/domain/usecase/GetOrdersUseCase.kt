package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase(private val repository: OrderRepository) {
    operator fun invoke(): Flow<List<Order>> = repository.getOrders()
}
