package com.example.kit_market.presentation.screen.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.usecase.AddToCartUseCase
import com.example.kit_market.domain.usecase.GetCartUseCase
import com.example.kit_market.domain.usecase.GetProductByIdUseCase
import com.example.kit_market.domain.usecase.UpdateCartItemQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productId: Long,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val getCartUseCase: GetCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    init {
        loadProduct()
        observeCart()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val product = getProductByIdUseCase(productId)
                _state.update { it.copy(product = product, isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Не удалось загрузить товар. Проверьте интернет-соединение.")
                }
            }
        }
    }

    fun retry() {
        loadProduct()
    }

    private fun observeCart() {
        viewModelScope.launch {
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
                viewModelScope.launch { addToCartUseCase(product) }
            }
            ProductDetailIntent.Increment -> {
                viewModelScope.launch {
                    updateCartItemQuantityUseCase(productId, _state.value.quantityInCart + 1)
                }
            }
            ProductDetailIntent.Decrement -> {
                viewModelScope.launch {
                    updateCartItemQuantityUseCase(productId, _state.value.quantityInCart - 1)
                }
            }
        }
    }
}
