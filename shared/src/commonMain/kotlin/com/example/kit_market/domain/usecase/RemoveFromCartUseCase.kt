package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.CartRepository

class RemoveFromCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: Long) = repository.removeFromCart(productId)
}
