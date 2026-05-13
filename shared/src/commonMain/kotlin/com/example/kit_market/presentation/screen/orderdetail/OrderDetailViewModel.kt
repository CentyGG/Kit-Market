package com.example.kit_market.presentation.screen.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.usecase.CancelOrderUseCase
import com.example.kit_market.domain.usecase.GetOrderByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    private val orderId: Long,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailState())
    val state = _state.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            try {
                val order = getOrderByIdUseCase(orderId)
                _state.update { it.copy(order = order, isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки заказа")
                }
            }
        }
    }

    fun cancelOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isCancelling = true) }
            try {
                cancelOrderUseCase(orderId)
                loadOrder()
            } catch (e: Exception) {
                _state.update { it.copy(isCancelling = false, error = "Не удалось отменить заказ") }
            }
        }
    }
}
