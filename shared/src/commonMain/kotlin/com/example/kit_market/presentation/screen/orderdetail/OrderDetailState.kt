package com.example.kit_market.presentation.screen.orderdetail

import com.example.kit_market.domain.model.Order

data class OrderDetailState(
    val order: Order? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
