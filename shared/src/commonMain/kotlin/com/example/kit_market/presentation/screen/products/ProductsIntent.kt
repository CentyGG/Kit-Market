package com.example.kit_market.presentation.screen.products

import com.example.kit_market.domain.model.Product

sealed interface ProductsIntent {
    data class Search(val query: String) : ProductsIntent
    data class AddToCart(val product: Product) : ProductsIntent
    data class Increment(val productId: Long) : ProductsIntent
    data class Decrement(val productId: Long) : ProductsIntent
    data object Retry : ProductsIntent
}
