package com.example.kit_market.presentation.screen.productdetail

sealed interface ProductDetailIntent {
    data object AddToCart : ProductDetailIntent
    data object Increment : ProductDetailIntent
    data object Decrement : ProductDetailIntent
}
