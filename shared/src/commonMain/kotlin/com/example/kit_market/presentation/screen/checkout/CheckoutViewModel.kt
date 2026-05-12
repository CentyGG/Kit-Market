package com.example.kit_market.presentation.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.usecase.ClearCartUseCase
import com.example.kit_market.domain.usecase.CreatePaymentUseCase
import com.example.kit_market.domain.usecase.GetCartUseCase
import com.example.kit_market.domain.usecase.PlaceOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state = _state.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            val items = getCartUseCase().first()
            val total = items.sumOf { it.product.price * it.quantity }
            _state.update { it.copy(items = items, totalPrice = total) }
        }
    }

    fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.SelectPaymentMethod -> {
                _state.update { it.copy(paymentMethod = intent.method) }
            }
            is CheckoutIntent.PlaceOrder -> placeOrder()
        }
    }

    private fun placeOrder() {
        val currentState = _state.value
        if (currentState.items.isEmpty() || currentState.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val order = placeOrderUseCase(currentState.items)
                clearCartUseCase()

                when (currentState.paymentMethod) {
                    PaymentMethod.CARD -> {
                        val payment = createPaymentUseCase(order.id)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orderCreatedId = order.id,
                                paymentUrl = payment.paymentUrl,
                                paymentId = payment.paymentId
                            )
                        }
                    }
                    PaymentMethod.CASH -> {
                        _state.update {
                            it.copy(isLoading = false, orderCreatedId = order.id)
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка при оформлении заказа")
                }
            }
        }
    }
}
