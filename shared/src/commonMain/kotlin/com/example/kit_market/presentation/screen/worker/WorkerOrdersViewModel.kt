package com.example.kit_market.presentation.screen.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.domain.usecase.GetWorkerOrdersUseCase
import com.example.kit_market.domain.usecase.UpdateOrderStatusUseCase
import com.example.kit_market.domain.usecase.WorkerCancelOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkerOrdersViewModel(
    private val getWorkerOrdersUseCase: GetWorkerOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val workerCancelOrderUseCase: WorkerCancelOrderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkerOrdersState())
    val state = _state.asStateFlow()

    private var allOrders: List<Order> = emptyList()

    init {
        loadOrders()
    }

    fun selectTab(tab: WorkerTab) {
        _state.update { it.copy(selectedTab = tab, orders = filterOrders(tab)) }
    }

    fun refresh() {
        loadOrders()
    }

    fun updateStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val success = updateOrderStatusUseCase(orderId, newStatus)
                if (success) {
                    loadOrders()
                }
            } catch (_: Exception) {
                _state.update { it.copy(error = "Не удалось обновить статус") }
            }
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            try {
                val success = workerCancelOrderUseCase(orderId)
                if (success) {
                    loadOrders()
                }
            } catch (_: Exception) {
                _state.update { it.copy(error = "Не удалось отменить заказ") }
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                allOrders = getWorkerOrdersUseCase()
                _state.update {
                    it.copy(
                        isLoading = false,
                        orders = filterOrders(it.selectedTab)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Не удалось загрузить заказы")
                }
            }
        }
    }

    private fun filterOrders(tab: WorkerTab): List<Order> {
        return when (tab) {
            WorkerTab.NEW -> allOrders.filter {
                it.status == OrderStatus.CREATED || it.status == OrderStatus.PAID
            }
            WorkerTab.ASSEMBLING -> allOrders.filter { it.status == OrderStatus.ASSEMBLING }
            WorkerTab.READY -> allOrders.filter { it.status == OrderStatus.READY }
            WorkerTab.HISTORY -> allOrders.filter {
                it.status == OrderStatus.COMPLETED || it.status == OrderStatus.CANCELLED
            }
        }
    }
}
