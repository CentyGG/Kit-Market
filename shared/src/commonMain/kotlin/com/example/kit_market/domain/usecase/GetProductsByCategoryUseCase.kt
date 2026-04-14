package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.ProductRepository

class GetProductsByCategoryUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(categoryId: Long): List<Product> =
        repository.getProductsByCategory(categoryId)
}
