package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.PaymentInfo
import com.example.kit_market.domain.repository.OrderRepository

class CreatePaymentUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: Long): PaymentInfo = repository.createPayment(orderId)
}
