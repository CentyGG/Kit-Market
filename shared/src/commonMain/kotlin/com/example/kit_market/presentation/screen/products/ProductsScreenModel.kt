package com.example.kit_market.presentation.screen.products

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsScreenModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val getCartUseCase: GetCartUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    init {
        loadData()
        observeCart()
    }

    private fun loadData() {
        screenModelScope.launch {
            val categories = getCategoriesUseCase()
            val productsByCategory = mutableMapOf<Long, List<Product>>()
            categories.forEach { category ->
                val products = getProductsByCategoryUseCase(category.id)
                productsByCategory[category.id] = products.take(6)
            }
            _state.update {
                it.copy(
                    categories = categories,
                    productsByCategory = productsByCategory,
                    isLoading = false
                )
            }
        }
    }

    private fun observeCart() {
        screenModelScope.launch {
            getCartUseCase().collect { cartItems ->
                val quantities = cartItems.associate { it.product.id to it.quantity }
                _state.update { it.copy(cartQuantities = quantities) }
            }
        }
    }

    fun onIntent(intent: ProductsIntent) {
        when (intent) {
            is ProductsIntent.Search -> search(intent.query)
            is ProductsIntent.AddToCart -> addToCart(intent.product)
            is ProductsIntent.Increment -> increment(intent.productId)
            is ProductsIntent.Decrement -> decrement(intent.productId)
        }
    }

    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query, isSearching = query.isNotBlank()) }
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        screenModelScope.launch {
            val results = searchProductsUseCase(query)
            _state.update { it.copy(searchResults = results) }
        }
    }

    private fun addToCart(product: Product) {
        screenModelScope.launch { addToCartUseCase(product) }
    }

    private fun increment(productId: Long) {
        screenModelScope.launch {
            val currentQty = _state.value.cartQuantities[productId] ?: 0
            updateCartItemQuantityUseCase(productId, currentQty + 1)
        }
    }

    private fun decrement(productId: Long) {
        screenModelScope.launch {
            val currentQty = _state.value.cartQuantities[productId] ?: 0
            updateCartItemQuantityUseCase(productId, currentQty - 1)
        }
    }
}
