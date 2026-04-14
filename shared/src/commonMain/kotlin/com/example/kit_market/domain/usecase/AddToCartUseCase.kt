package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.CartRepository

class AddToCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(product: Product) = repository.addToCart(product)
}
