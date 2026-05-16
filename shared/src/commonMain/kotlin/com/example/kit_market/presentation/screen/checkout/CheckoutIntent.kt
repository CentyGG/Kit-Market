package com.example.kit_market.presentation.screen.checkout

sealed interface CheckoutIntent {
    data class SelectPaymentMethod(val method: PaymentMethod) : CheckoutIntent
    data class SelectPickupDate(val date: String) : CheckoutIntent
    data class SelectPickupTime(val time: String) : CheckoutIntent
    data object PlaceOrder : CheckoutIntent
    data object RetryPayment : CheckoutIntent
}
