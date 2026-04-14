package com.example.kit_market.data.repository

import com.example.kit_market.data.fake.FakeProducts
import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.ProductRepository

class ProductRepositoryImpl : ProductRepository {

    override suspend fun getCategories(): List<Category> = FakeProducts.categories

    override suspend fun getProductsByCategory(categoryId: Long): List<Product> =
        FakeProducts.products.filter { it.categoryId == categoryId }

    override suspend fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return FakeProducts.products
        return FakeProducts.products.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    override suspend fun getProductById(id: Long): Product? =
        FakeProducts.products.find { it.id == id }
}
