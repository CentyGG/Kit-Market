package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.repository.ProductRepository

class GetCategoriesUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): List<Category> = repository.getCategories()
}
