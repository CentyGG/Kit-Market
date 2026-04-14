package com.example.kit_market.presentation.screen.productdetail

import com.example.kit_market.domain.model.Product

data class ProductDetailState(
    val product: Product? = null,
    val quantityInCart: Int = 0,
    val isLoading: Boolean = true
)
