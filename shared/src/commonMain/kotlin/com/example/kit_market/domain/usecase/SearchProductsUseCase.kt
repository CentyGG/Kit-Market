package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.ProductRepository

class SearchProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(query: String): List<Product> =
        repository.searchProducts(query)
}
