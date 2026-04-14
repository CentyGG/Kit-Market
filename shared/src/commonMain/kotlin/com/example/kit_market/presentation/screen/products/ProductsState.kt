package com.example.kit_market.presentation.screen.products

import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.model.Product

data class ProductsState(
    val categories: List<Category> = emptyList(),
    val productsByCategory: Map<Long, List<Product>> = emptyMap(),
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val isSearching: Boolean = false,
    val cartQuantities: Map<Long, Int> = emptyMap(),
    val isLoading: Boolean = true
)
