package com.example.kit_market.domain.repository

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun placeOrder(items: List<CartItem>): Order
    fun getOrders(): Flow<List<Order>>
}
