package com.example.kit_market.presentation.screen.orders

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.usecase.GetOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdersScreenModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(OrdersState())
    val state = _state.asStateFlow()

    init {
        screenModelScope.launch {
            getOrdersUseCase().collect { orders ->
                _state.update {
                    it.copy(
                        orders = orders.sortedByDescending { order -> order.date },
                        isLoading = false
                    )
                }
            }
        }
    }
}
