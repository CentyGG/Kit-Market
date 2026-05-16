package com.example.kit_market.presentation.screen.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.domain.usecase.CancelOrderUseCase
import com.example.kit_market.domain.usecase.GetOrderByIdUseCase
import com.example.kit_market.notification.NotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var pollingJob: Job? = null
    private var countdownJob: Job? = null

    companion object {
        private const val PAYMENT_TIMEOUT_SECONDS = 600 // 10 минут
        private const val POLL_INTERVAL_MS = 15_000L // проверка каждые 15 сек
    }

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val order = getOrderByIdUseCase(orderId)
                _state.update { it.copy(order = order, isLoading = false) }
                if (order.status == OrderStatus.CREATED && order.paymentType == "card") {
                    startPaymentTimer()
                } else {
                    stopTimers()
                    _state.update { it.copy(paymentTimeLeftSeconds = null) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Не удалось загрузить заказ. Проверьте интернет-соединение.")
                }
            }
        }
    }

    private fun startPaymentTimer() {
        stopTimers()

        // Обратный отсчёт — обновляем каждую секунду
        countdownJob = viewModelScope.launch {
            var secondsLeft = PAYMENT_TIMEOUT_SECONDS
            _state.update { it.copy(paymentTimeLeftSeconds = secondsLeft) }

            while (secondsLeft > 0) {
                delay(1000L)
                secondsLeft--
                _state.update { it.copy(paymentTimeLeftSeconds = secondsLeft) }
            }

            // Время вышло — проверяем статус и отменяем если не оплачен
            _state.update { it.copy(paymentTimeLeftSeconds = null) }
            try {
                val order = getOrderByIdUseCase(orderId)
                if (order.status == OrderStatus.CREATED) {
                    // Не оплачен за 10 минут — отменяем
                    try {
                        cancelOrderUseCase(orderId)
                        NotificationManager.showOrderStatusChangedIfEnabled(orderId, OrderStatus.CANCELLED)
                    } catch (_: Exception) { }
                    loadOrder()
                } else {
                    _state.update { it.copy(order = order) }
                }
            } catch (_: Exception) {
                loadOrder()
            }
        }

        // Polling статуса — проверяем каждые 15 секунд
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(POLL_INTERVAL_MS)
                try {
                    val order = getOrderByIdUseCase(orderId)
                    val previousStatus = _state.value.order?.status
                    _state.update { it.copy(order = order) }
                    if (order.status != OrderStatus.CREATED) {
                        // Оплата прошла или статус изменился — останавливаем всё
                        stopTimers()
                        _state.update { it.copy(paymentTimeLeftSeconds = null) }
                        if (previousStatus != null && previousStatus != order.status) {
                            try {
                                NotificationManager.showOrderStatusChangedIfEnabled(orderId, order.status)
                            } catch (_: Exception) { }
                        }
                        return@launch
                    }
                } catch (_: Exception) {
                    // Сеть недоступна — продолжаем
                }
            }
        }
    }

    private fun stopTimers() {
        pollingJob?.cancel()
        pollingJob = null
        countdownJob?.cancel()
        countdownJob = null
    }

    fun retry() {
        loadOrder()
    }

    fun refreshOrder() {
        viewModelScope.launch {
            try {
                val previousStatus = _state.value.order?.status
                val order = getOrderByIdUseCase(orderId)
                _state.update { it.copy(order = order, error = null) }
                if (previousStatus != null && previousStatus != order.status) {
                    try {
                        NotificationManager.showOrderStatusChangedIfEnabled(orderId, order.status)
                    } catch (_: Exception) { }
                }
                if (order.status == OrderStatus.CREATED && order.paymentType == "card") {
                    if (countdownJob == null || countdownJob?.isActive != true) {
                        startPaymentTimer()
                    }
                } else {
                    stopTimers()
                    _state.update { it.copy(paymentTimeLeftSeconds = null) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Не удалось обновить заказ. Проверьте интернет-соединение.")
                }
            }
        }
    }

    fun cancelOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isCancelling = true, error = null) }
            try {
                cancelOrderUseCase(orderId)
                stopTimers()
                loadOrder()
            } catch (e: Exception) {
                _state.update {
                    it.copy(isCancelling = false, error = "Не удалось отменить заказ. Проверьте интернет-соединение.")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimers()
    }
}
