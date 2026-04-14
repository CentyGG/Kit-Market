package com.example.kit_market.data.repository

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class OrderRepositoryImpl : OrderRepository {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    private var nextId = 1L

    override suspend fun placeOrder(items: List<CartItem>): Order {
        val totalPrice = items.sumOf { it.product.price * it.quantity }
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val order = Order(
            id = nextId++,
            items = items,
            totalPrice = totalPrice,
            date = now,
            status = OrderStatus.PROCESSING
        )
        _orders.update { it + order }
        return order
    }

    override fun getOrders(): Flow<List<Order>> = _orders.asStateFlow()
}
