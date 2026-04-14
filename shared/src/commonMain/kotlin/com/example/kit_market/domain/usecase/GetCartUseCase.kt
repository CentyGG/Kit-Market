package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class GetCartUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<List<CartItem>> = repository.getCart()
}
