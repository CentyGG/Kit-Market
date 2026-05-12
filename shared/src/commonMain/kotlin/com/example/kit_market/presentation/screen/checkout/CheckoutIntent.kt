package com.example.kit_market.presentation.screen.checkout

sealed interface CheckoutIntent {
    data class SelectPaymentMethod(val method: PaymentMethod) : CheckoutIntent
    data object PlaceOrder : CheckoutIntent
}
