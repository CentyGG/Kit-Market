package com.example.kit_market.presentation.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.domain.usecase.ClearCartUseCase
import com.example.kit_market.domain.usecase.CreatePaymentUseCase
import com.example.kit_market.domain.usecase.GetCartUseCase
import com.example.kit_market.domain.usecase.GetOrderByIdUseCase
import com.example.kit_market.domain.usecase.PlaceOrderUseCase
import com.example.kit_market.notification.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state = _state.asStateFlow()

    private val khabarovskTz = TimeZone.of("Asia/Vladivostok")

    init {
        loadCart()
        initPickupDates()
    }

    private fun initPickupDates() {
        val now = Clock.System.now().toLocalDateTime(khabarovskTz)
        val dates = mutableListOf<String>()
        // Up to 7 days ahead
        for (dayOffset in 0..6) {
            val date = now.date.plus(dayOffset, DateTimeUnit.DAY)
            dates.add(date.toString())
        }
        val firstDate = dates.first()
        val slots = computeTimeSlots(firstDate)
        // If today has no slots, start from tomorrow
        val selectedDate = if (slots.isEmpty() && dates.size > 1) dates[1] else firstDate
        val selectedSlots = if (slots.isEmpty() && dates.size > 1) computeTimeSlots(dates[1]) else slots
        _state.update {
            it.copy(
                availableDates = dates,
                selectedDate = selectedDate,
                availableTimeSlots = selectedSlots,
                selectedTime = selectedSlots.firstOrNull()
            )
        }
    }

    private fun computeTimeSlots(dateStr: String): List<String> {
        val date = LocalDate.parse(dateStr)
        val now = Clock.System.now().toLocalDateTime(khabarovskTz)
        val slots = mutableListOf<String>()
        // Working hours: 9:00 - 22:00, step 30 min, last slot 21:30
        var hour = 9
        var minute = 0
        while (hour < 22) {
            val slotTime = LocalTime(hour, minute)
            val slotDateTime = LocalDateTime(date, slotTime)

            // Only future slots with 30 min buffer
            if (date > now.date || (date == now.date && slotDateTime > now.let {
                    // Add 30 minutes to now
                    val nowInstant = Clock.System.now()
                    val futureInstant = nowInstant.plus(30, DateTimeUnit.MINUTE, khabarovskTz)
                    futureInstant.toLocalDateTime(khabarovskTz)
                })) {
                slots.add("${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")
            }

            minute += 30
            if (minute >= 60) {
                minute = 0
                hour++
            }
        }
        return slots
    }

    private fun loadCart() {
        viewModelScope.launch {
            try {
                val items = getCartUseCase().first()
                val total = items.sumOf { it.product.price * it.quantity }
                _state.update { it.copy(items = items, totalPrice = total) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Не удалось загрузить корзину") }
            }
        }
    }

    fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            is CheckoutIntent.SelectPaymentMethod -> {
                _state.update { it.copy(paymentMethod = intent.method) }
            }
            is CheckoutIntent.SelectPickupDate -> {
                val slots = computeTimeSlots(intent.date)
                _state.update {
                    it.copy(
                        selectedDate = intent.date,
                        availableTimeSlots = slots,
                        selectedTime = slots.firstOrNull()
                    )
                }
            }
            is CheckoutIntent.SelectPickupTime -> {
                _state.update { it.copy(selectedTime = intent.time) }
            }
            is CheckoutIntent.PlaceOrder -> placeOrder()
            is CheckoutIntent.RetryPayment -> retryPayment()
        }
    }

    private fun placeOrder() {
        val currentState = _state.value
        if (currentState.items.isEmpty() || currentState.isLoading) return

        // Если заказ уже создан — не дублируем, а повторяем получение ссылки на оплату
        if (currentState.orderCreatedId != null && currentState.paymentMethod == PaymentMethod.CARD) {
            retryPayment()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val paymentType = when (currentState.paymentMethod) {
                PaymentMethod.CASH -> "cash"
                PaymentMethod.CARD -> "card"
            }

            // Формируем время самовывоза
            val pickupTime = if (currentState.selectedDate != null && currentState.selectedTime != null) {
                "${currentState.selectedDate} ${currentState.selectedTime}"
            } else null

            // Шаг 1: создаём заказ
            val order = try {
                placeOrderUseCase(currentState.items, paymentType, pickupTime)
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Не удалось создать заказ. Проверьте интернет-соединение.")
                }
                return@launch
            }

            try {
                NotificationManager.showOrderCreatedIfEnabled(order.id, currentState.totalPrice)
            } catch (_: Exception) { }

            when (currentState.paymentMethod) {
                PaymentMethod.CARD -> {
                    // Шаг 2: создаём платёж (заказ уже создан — сохраняем orderId)
                    try {
                        val payment = createPaymentUseCase(order.id)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orderCreatedId = order.id,
                                paymentUrl = payment.paymentUrl,
                                paymentId = payment.paymentId
                            )
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orderCreatedId = order.id,
                                error = "Заказ создан, но не удалось получить ссылку на оплату. Попробуйте ещё раз."
                            )
                        }
                    }
                }
                PaymentMethod.CASH -> {
                    try { clearCartUseCase() } catch (_: Exception) { }
                    _state.update {
                        it.copy(isLoading = false, orderCreatedId = order.id)
                    }
                }
            }
        }
    }

    /** Повторная попытка получить ссылку на оплату для уже созданного заказа */
    private fun retryPayment() {
        val orderId = _state.value.orderCreatedId ?: return
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Сначала проверяем статус заказа — может, он уже оплачен или отменён
            try {
                val order = getOrderByIdUseCase(orderId)
                when (order.status) {
                    OrderStatus.PAID, OrderStatus.ASSEMBLING, OrderStatus.READY, OrderStatus.COMPLETED -> {
                        // Заказ уже оплачен — не создаём новый платёж
                        try { clearCartUseCase() } catch (_: Exception) { }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orderPaid = true
                            )
                        }
                        return@launch
                    }
                    OrderStatus.CANCELLED -> {
                        // Заказ отменён — сбрасываем состояние, чтобы можно было создать новый
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orderCreatedId = null,
                                paymentUrl = null,
                                paymentId = null,
                                error = "Заказ был отменён. Оформите заказ заново."
                            )
                        }
                        return@launch
                    }
                    OrderStatus.CREATED -> { /* продолжаем — создаём платёж */ }
                }
            } catch (_: Exception) {
                // Не удалось проверить статус — не рискуем создавать платёж
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось проверить статус заказа. Проверьте интернет-соединение."
                    )
                }
                return@launch
            }

            try {
                val payment = createPaymentUseCase(orderId)
                _state.update {
                    it.copy(
                        isLoading = false,
                        paymentUrl = payment.paymentUrl,
                        paymentId = payment.paymentId
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось получить ссылку на оплату. Проверьте интернет-соединение."
                    )
                }
            }
        }
    }
}
