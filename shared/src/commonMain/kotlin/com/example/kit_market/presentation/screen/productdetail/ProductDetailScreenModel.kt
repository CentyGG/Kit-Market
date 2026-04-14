package com.example.kit_market.presentation.screen.productdetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.usecase.AddToCartUseCase
import com.example.kit_market.domain.usecase.GetCartUseCase
import com.example.kit_market.domain.usecase.GetProductByIdUseCase
import com.example.kit_market.domain.usecase.UpdateCartItemQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailScreenModel(
    private val productId: Long,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val getCartUseCase: GetCartUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    init {
        loadProduct()
        observeCart()
    }

    private fun loadProduct() {
        screenModelScope.launch {
            val product = getProductByIdUseCase(productId)
            _state.update { it.copy(product = product, isLoading = false) }
        }
    }

    private fun observeCart() {
        screenModelScope.launch {
            getCartUseCase().collect { cartItems ->
                val qty = cartItems.find { it.product.id == productId }?.quantity ?: 0
                _state.update { it.copy(quantityInCart = qty) }
            }
        }
    }

    fun onIntent(intent: ProductDetailIntent) {
        when (intent) {
            ProductDetailIntent.AddToCart -> {
                val product = _state.value.product ?: return
                screenModelScope.launch { addToCartUseCase(product) }
            }
            ProductDetailIntent.Increment -> {
                screenModelScope.launch {
                    updateCartItemQuantityUseCase(productId, _state.value.quantityInCart + 1)
                }
            }
            ProductDetailIntent.Decrement -> {
                screenModelScope.launch {
                    updateCartItemQuantityUseCase(productId, _state.value.quantityInCart - 1)
                }
            }
        }
    }
}
