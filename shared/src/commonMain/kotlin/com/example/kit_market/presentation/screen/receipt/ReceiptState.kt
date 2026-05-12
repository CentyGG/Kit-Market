package com.example.kit_market.presentation.screen.receipt

import com.example.kit_market.domain.model.OrderItem

data class ReceiptState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val status: String = "wait",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val receiptDatetime: String? = null,
    val fnNumber: String? = null,
    val fiscalDocumentNumber: Int? = null,
    val fiscalDocumentAttribute: Long? = null,
    val checkUrl: String? = null
)
