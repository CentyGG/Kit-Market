package com.example.kit_market.data.repository

import com.example.kit_market.domain.model.User
import com.example.kit_market.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl : UserRepository {

    private val _user = MutableStateFlow<User?>(null)
    private var pendingPhone: String? = null

    override fun getUser(): Flow<User?> = _user.asStateFlow()

    override suspend fun getCurrentUser(): User? = _user.value

    override suspend fun login(phone: String) {
        pendingPhone = phone
    }

    override suspend fun verifyCode(code: String): Boolean {
        if (code.length != 6) return false
        val phone = pendingPhone ?: return false
        _user.value = User(phone = phone, firstName = "", lastName = "")
        pendingPhone = null
        return true
    }

    override suspend fun updateUser(user: User) {
        _user.value = user
    }

    override suspend fun logout() {
        _user.value = null
    }
}
