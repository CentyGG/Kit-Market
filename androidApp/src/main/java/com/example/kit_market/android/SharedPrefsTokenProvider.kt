package com.example.kit_market.android

import android.content.Context
import android.content.SharedPreferences
import com.example.kit_market.data.remote.TokenProvider

class SharedPrefsTokenProvider(context: Context) : TokenProvider {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kit_market_auth", Context.MODE_PRIVATE)
    override fun getToken(): String? = prefs.getString("token", null)
    override fun setToken(token: String?) {
        prefs.edit().apply {
            if (token != null) putString("token", token) else remove("token")
        }.apply()
    }
    override fun getUserId(): Long? {
        val id = prefs.getLong("user_id", -1L)
        return if (id == -1L) null else id
    }
    override fun setUserId(userId: Long?) {
        prefs.edit().apply {
            if (userId != null) putLong("user_id", userId) else remove("user_id")
        }.apply()
    }
    override fun getRole(): String? = prefs.getString("role", null)
    override fun setRole(role: String?) {
        prefs.edit().apply {
            if (role != null) putString("role", role) else remove("role")
        }.apply()
    }
}
