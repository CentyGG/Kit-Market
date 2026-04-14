package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.CartRepository

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke() = repository.clearCart()
}
