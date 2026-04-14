package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.CartRepository

class UpdateCartItemQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: Long, quantity: Int) =
        repository.updateQuantity(productId, quantity)
}
