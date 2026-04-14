package com.example.kit_market.domain.repository

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(): Flow<List<CartItem>>
    suspend fun addToCart(product: Product)
    suspend fun updateQuantity(productId: Long, quantity: Int)
    suspend fun removeFromCart(productId: Long)
    suspend fun clearCart()
}
