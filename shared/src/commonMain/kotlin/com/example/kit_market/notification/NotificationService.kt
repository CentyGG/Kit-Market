package com.example.kit_market.notification

import com.example.kit_market.domain.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

expect class NotificationService {
    fun showOrderCreated(orderId: Long, totalPrice: Double)
    fun showOrderStatusChanged(orderId: Long, status: OrderStatus)
}

interface NotificationSettingsProvider {
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
}

object NotificationManager {
    private var _service: NotificationService? = null
    private var _settingsProvider: NotificationSettingsProvider? = null
    val service: NotificationService get() = _service ?: error("NotificationService not initialized")

    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    fun init(service: NotificationService, settingsProvider: NotificationSettingsProvider) {
        _service = service
        _settingsProvider = settingsProvider
        _enabled.value = settingsProvider.isEnabled()
    }

    fun setEnabled(value: Boolean) {
        _settingsProvider?.setEnabled(value)
        _enabled.value = value
    }

    fun showOrderCreatedIfEnabled(orderId: Long, totalPrice: Double) {
        if (_enabled.value) {
            _service?.showOrderCreated(orderId, totalPrice)
        }
    }

    fun showOrderStatusChangedIfEnabled(orderId: Long, status: OrderStatus) {
        if (_enabled.value) {
            _service?.showOrderStatusChanged(orderId, status)
        }
    }
}
