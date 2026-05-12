package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.PaymentStatus
import com.example.kit_market.domain.repository.OrderRepository

class GetPaymentStatusUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(paymentId: String): PaymentStatus = repository.getPaymentStatus(paymentId)
}
