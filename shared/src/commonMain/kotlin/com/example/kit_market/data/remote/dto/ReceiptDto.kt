package com.example.kit_market.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDetailResponse(
    val orderId: Long,
    val status: String,
    val total: Long?,
    val receiptDatetime: String?,
    val fnNumber: String?,
    val fiscalDocumentNumber: Int?,
    val fiscalDocumentAttribute: Long?,
    val checkUrl: String?
)
