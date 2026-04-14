package com.example.kit_market.presentation.screen.cart

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.usecase.GetCartUseCase
import com.example.kit_market.domain.usecase.RemoveFromCartUseCase
import com.example.kit_market.domain.usecase.UpdateCartItemQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartScreenModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(CartState())
    val state = _state.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        screenModelScope.launch {
            getCartUseCase().collect { items ->
                val total = items.sumOf { it.product.price * it.quantity }
                _state.update {
                    it.copy(items = items, totalPrice = total, isLoading = false)
                }
            }
        }
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.Increment -> {
                screenModelScope.launch {
                    val item = _state.value.items.find { it.product.id == intent.productId }
                    if (item != null) {
                        updateCartItemQuantityUseCase(intent.productId, item.quantity + 1)
                    }
                }
            }
            is CartIntent.Decrement -> {
                screenModelScope.launch {
                    val item = _state.value.items.find { it.product.id == intent.productId }
                    if (item != null) {
                        if (item.quantity <= 1) {
                            removeFromCartUseCase(intent.productId)
                        } else {
                            updateCartItemQuantityUseCase(intent.productId, item.quantity - 1)
                        }
                    }
                }
            }
            is CartIntent.Remove -> {
                screenModelScope.launch { removeFromCartUseCase(intent.productId) }
            }
        }
    }
}
