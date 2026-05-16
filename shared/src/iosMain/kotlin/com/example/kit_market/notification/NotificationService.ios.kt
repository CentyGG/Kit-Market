package com.example.kit_market.notification

import com.example.kit_market.domain.model.OrderStatus
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNTimeIntervalNotificationTrigger

actual class NotificationService {

    private val center = UNUserNotificationCenter.currentNotificationCenter()
    private var permissionRequested = false

    private fun ensurePermission() {
        if (permissionRequested) return
        permissionRequested = true
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ -> }
    }

    actual fun showOrderCreated(orderId: Long, totalPrice: Double) {
        ensurePermission()
        val content = UNMutableNotificationContent().apply {
            setTitle("Заказ оформлен")
            setBody("Заказ #$orderId на ${"%.0f".format(totalPrice)} ₽ успешно создан")
            setSound(UNNotificationSound.defaultSound())
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(
            "order_created_$orderId",
            content,
            trigger
        )
        center.addNotificationRequest(request, null)
    }

    actual fun showOrderStatusChanged(orderId: Long, status: OrderStatus) {
        ensurePermission()
        val content = UNMutableNotificationContent().apply {
            setTitle("Статус заказа обновлён")
            setBody("Заказ #$orderId: ${status.toRussian()}")
            setSound(UNNotificationSound.defaultSound())
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(
            "order_status_${orderId}_${status.name}",
            content,
            trigger
        )
        center.addNotificationRequest(request, null)
    }
}
