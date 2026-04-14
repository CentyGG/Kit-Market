package com.example.kit_market.domain.repository

import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.model.Product

interface ProductRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getProductsByCategory(categoryId: Long): List<Product>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun getProductById(id: Long): Product?
}
