package com.example.kit_market.domain.model

data class PaymentInfo(
    val paymentId: String,
    val paymentUrl: String
)

data class PaymentStatus(
    val paymentId: String,
    val status: String,
    val amount: Long
)
