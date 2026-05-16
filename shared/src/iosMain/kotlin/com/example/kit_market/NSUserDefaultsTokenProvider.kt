package com.example.kit_market

import com.example.kit_market.data.remote.TokenProvider
import platform.Foundation.NSUserDefaults

class NSUserDefaultsTokenProvider : TokenProvider {
    private val defaults = NSUserDefaults.standardUserDefaults
    override fun getToken(): String? = defaults.stringForKey(KEY_TOKEN)
    override fun setToken(token: String?) {
        if (token != null) {
            defaults.setObject(token, forKey = KEY_TOKEN)
        } else {
            defaults.removeObjectForKey(KEY_TOKEN)
        }
    }
    override fun getUserId(): Long? {
        val hasKey = defaults.objectForKey(KEY_USER_ID) != null
        return if (hasKey) defaults.integerForKey(KEY_USER_ID) else null
    }
    override fun setUserId(userId: Long?) {
        if (userId != null) {
            defaults.setInteger(userId, forKey = KEY_USER_ID)
        } else {
            defaults.removeObjectForKey(KEY_USER_ID)
        }
    }
    private companion object {
        const val KEY_TOKEN = "kit_market_token"
        const val KEY_USER_ID = "kit_market_user_id"
    }
}
