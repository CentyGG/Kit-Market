package com.example.kit_market.presentation.screen.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val getCartUseCase: GetCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    init {
        loadData()
        observeCart()
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            try {
                val categories = getCategoriesUseCase()
                val productsByCategory = mutableMapOf<String, List<Product>>()
                categories.forEach { category ->
                    val products = getProductsByCategoryUseCase(category.name)
                    productsByCategory[category.name] = products.take(6)
                }
                val hasProducts = productsByCategory.values.any { it.isNotEmpty() }
                _state.update {
                    it.copy(
                        categories = categories,
                        productsByCategory = productsByCategory,
                        isLoading = false,
                        isError = !hasProducts
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
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
            is ProductsIntent.Retry -> loadData()
        }
    }

    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query, isSearching = query.isNotBlank()) }
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        viewModelScope.launch {
            val results = searchProductsUseCase(query)
            _state.update { it.copy(searchResults = results) }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch { addToCartUseCase(product) }
    }

    private fun increment(productId: Long) {
        viewModelScope.launch {
            val currentQty = _state.value.cartQuantities[productId] ?: 0
            updateCartItemQuantityUseCase(productId, currentQty + 1)
        }
    }

    private fun decrement(productId: Long) {
        viewModelScope.launch {
            val currentQty = _state.value.cartQuantities[productId] ?: 0
            updateCartItemQuantityUseCase(productId, currentQty - 1)
        }
    }
}
