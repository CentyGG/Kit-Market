package com.example.kit_market.android

import android.content.Context
import com.example.kit_market.notification.NotificationSettingsProvider

class SharedPrefsNotificationSettings(context: Context) : NotificationSettingsProvider {
    private val prefs = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)

    override fun isEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)

    override fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }
}
