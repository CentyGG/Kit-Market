package com.example.kit_market

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kit_market.data.remote.TokenStorage
import com.example.kit_market.notification.NotificationManager
import com.example.kit_market.notification.NotificationService
import com.example.kit_market.notification.NSUserDefaultsNotificationSettings

fun MainViewController(): platform.UIKit.UIViewController {
    TokenStorage.init(NSUserDefaultsTokenProvider())
    NotificationManager.init(NotificationService(), NSUserDefaultsNotificationSettings())
    return ComposeUIViewController { App() }
}
