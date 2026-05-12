package com.example.kit_market.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentRequest(val orderId: Long)

@Serializable
data class PaymentResponse(val paymentId: String, val paymentUrl: String)

@Serializable
data class PaymentStatusResponse(
    val paymentId: String,
    val status: String,
    val amount: Long
)

@Serializable
data class PaymentConfirmResponse(
    val status: String,
    val receiptUuid: String? = null,
    val receiptError: String? = null
)
