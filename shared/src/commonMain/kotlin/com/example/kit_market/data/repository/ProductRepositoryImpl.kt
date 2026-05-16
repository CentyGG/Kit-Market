package com.example.kit_market.data.repository

import com.example.kit_market.data.remote.ApiService
import com.example.kit_market.data.remote.dto.ProductResponse
import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.repository.ProductRepository
import com.example.kit_market.domain.util.fuzzyMatchScore

class ProductRepositoryImpl(
    private val apiService: ApiService
) : ProductRepository {

    private var cachedProducts: List<Product>? = null

    override suspend fun getCategories(): List<Category> {
        val products = getAllProducts()
        val categoryNames = products.map { it.category }
            .distinct()
            .filter { it.isNotBlank() }
        if (categoryNames.isEmpty()) {
            return listOf(Category(name = "Все товары"))
        }
        return categoryNames.map { Category(name = it) }
    }

    override suspend fun getProductsByCategory(categoryName: String): List<Product> {
        val products = getAllProducts()
        if (categoryName == "Все товары") return products
        return products.filter { it.category == categoryName }
    }

    override suspend fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return getAllProducts()

        val all = getAllProducts()
        return all.mapNotNull { product ->
            val score = fuzzyMatchScore(query, product.name)
            if (score != null) product to score else null
        }
            .sortedBy { it.second }
            .map { it.first }
    }

    override suspend fun getProductById(id: Long): Product? {
        return getAllProducts().find { it.id == id }
    }

    override fun clearCache() {
        cachedProducts = null
    }

    private suspend fun getAllProducts(): List<Product> {
        cachedProducts?.let { return it }
        val products = apiService.getProducts().map { it.toDomain() }
        cachedProducts = products
        return products
    }

    private fun ProductResponse.toDomain(): Product = Product(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        price = price.toDouble() / 100.0,
        category = category,
        subcategory = subcategory
    )
}
