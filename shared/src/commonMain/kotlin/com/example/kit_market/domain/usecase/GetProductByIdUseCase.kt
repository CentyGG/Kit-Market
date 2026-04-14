package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.ProductRepository

class GetProductByIdUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): Product? = repository.getProductById(id)
}
