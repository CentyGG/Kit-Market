package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.repository.OrderRepository

class GetWorkerOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(status: String? = null): List<Order> =
        repository.getWorkerOrders(status)
}
