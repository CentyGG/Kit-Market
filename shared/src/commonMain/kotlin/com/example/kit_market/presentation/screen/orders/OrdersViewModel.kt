package com.example.kit_market.presentation.screen.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.usecase.GetOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersState())
    val state = _state.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val orders = getOrdersUseCase()
                _state.update {
                    it.copy(orders = orders, isLoading = false)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Не удалось загрузить заказы. Проверьте интернет-соединение.")
                }
            }
        }
    }

    fun retry() {
        loadOrders()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val orders = getOrdersUseCase()
                _state.update {
                    it.copy(orders = orders, error = null)
                }
            } catch (_: Exception) {
                // При фоновом обновлении не показываем ошибку, если данные уже есть
                if (_state.value.orders.isEmpty()) {
                    _state.update {
                        it.copy(error = "Не удалось загрузить заказы. Проверьте интернет-соединение.")
                    }
                }
            }
        }
    }
}
