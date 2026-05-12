package com.example.kit_market.data.remote

object TokenStorage {
    private var _tokenProvider: TokenProvider? = null

    var token: String?
        get() = _tokenProvider?.getToken()
        set(value) { _tokenProvider?.setToken(value) }

    var userId: Long?
        get() = _tokenProvider?.getUserId()
        set(value) { _tokenProvider?.setUserId(value) }

    fun init(provider: TokenProvider) {
        _tokenProvider = provider
    }
}

interface TokenProvider {
    fun getToken(): String?
    fun setToken(token: String?)
    fun getUserId(): Long?
    fun setUserId(userId: Long?)
}
