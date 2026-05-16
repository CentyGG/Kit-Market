package com.example.kit_market.presentation.screen.checkout

import com.example.kit_market.domain.model.CartItem

enum class PaymentMethod {
    CASH,
    CARD
}

data class CheckoutState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderCreatedId: Long? = null,
    val paymentUrl: String? = null,
    val paymentId: String? = null,
    val orderPaid: Boolean = false,
    val availableDates: List<String> = emptyList(),   // "2026-05-16", "2026-05-17", ...
    val selectedDate: String? = null,
    val availableTimeSlots: List<String> = emptyList(), // "09:00", "09:30", ...
    val selectedTime: String? = null
)
