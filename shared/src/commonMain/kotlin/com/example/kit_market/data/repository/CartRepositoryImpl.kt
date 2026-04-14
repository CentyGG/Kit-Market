package com.example.kit_market.data.repository

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartRepositoryImpl : CartRepository {

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())

    override fun getCart(): Flow<List<CartItem>> = _cart.asStateFlow()

    override suspend fun addToCart(product: Product) {
        _cart.update { items ->
            val existing = items.find { it.product.id == product.id }
            if (existing != null) {
                items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                items + CartItem(product = product, quantity = 1)
            }
        }
    }

    override suspend fun updateQuantity(productId: Long, quantity: Int) {
        _cart.update { items ->
            if (quantity <= 0) {
                items.filter { it.product.id != productId }
            } else {
                items.map {
                    if (it.product.id == productId) it.copy(quantity = quantity) else it
                }
            }
        }
    }

    override suspend fun removeFromCart(productId: Long) {
        _cart.update { items -> items.filter { it.product.id != productId } }
    }

    override suspend fun clearCart() {
        _cart.value = emptyList()
    }
}
