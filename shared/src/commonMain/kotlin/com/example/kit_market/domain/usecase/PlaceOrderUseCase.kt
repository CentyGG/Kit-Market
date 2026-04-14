package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.repository.OrderRepository

class PlaceOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(items: List<CartItem>): Order = repository.placeOrder(items)
}
