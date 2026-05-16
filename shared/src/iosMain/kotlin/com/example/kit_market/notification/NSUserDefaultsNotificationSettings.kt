package com.example.kit_market.notification

import platform.Foundation.NSUserDefaults

class NSUserDefaultsNotificationSettings : NotificationSettingsProvider {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun isEnabled(): Boolean {
        // Default to true if key doesn't exist
        return if (defaults.objectForKey("notifications_enabled") != null) {
            defaults.boolForKey("notifications_enabled")
        } else {
            true
        }
    }

    override fun setEnabled(enabled: Boolean) {
        defaults.setBool(enabled, forKey = "notifications_enabled")
    }
}
