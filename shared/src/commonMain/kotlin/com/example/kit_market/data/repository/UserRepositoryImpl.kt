package com.example.kit_market.data.repository

import com.example.kit_market.data.remote.ApiService
import com.example.kit_market.data.remote.TokenStorage
import com.example.kit_market.domain.model.User
import com.example.kit_market.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl(
    private val apiService: ApiService
) : UserRepository {

    private val _user = MutableStateFlow<User?>(null)
    private var pendingPhone: String? = null

    override fun getUser(): Flow<User?> = _user.asStateFlow()

    override suspend fun getCurrentUser(): User? {
        if (_user.value != null) return _user.value
        if (TokenStorage.token == null) return null
        return try {
            val response = apiService.getUser()
            val nameParts = response.name.split(" ", limit = 2)
            val user = User(
                id = response.id,
                phone = response.phone,
                firstName = nameParts.getOrElse(0) { "" },
                lastName = nameParts.getOrElse(1) { "" },
                role = response.role
            )
            TokenStorage.role = response.role
            _user.value = user
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun login(phone: String) {
        pendingPhone = phone
        apiService.sendCode(phone)
    }

    override suspend fun verifyCode(code: String): Boolean {
        val phone = pendingPhone ?: return false
        return try {
            val response = apiService.verifyCode(phone, code)
            TokenStorage.token = response.token
            // Fetch user info after login
            val userResponse = apiService.getUser()
            val nameParts = userResponse.name.split(" ", limit = 2)
            _user.value = User(
                id = userResponse.id,
                phone = userResponse.phone,
                firstName = nameParts.getOrElse(0) { "" },
                lastName = nameParts.getOrElse(1) { "" },
                role = userResponse.role
            )
            TokenStorage.userId = userResponse.id
            TokenStorage.role = userResponse.role
            pendingPhone = null
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUser(user: User) {
        try {
            val name = user.fullName
            val response = apiService.updateUser(name)
            val nameParts = response.name.split(" ", limit = 2)
            _user.value = User(
                id = response.id,
                phone = response.phone,
                firstName = nameParts.getOrElse(0) { "" },
                lastName = nameParts.getOrElse(1) { "" },
                role = response.role
            )
        } catch (e: Exception) {
            _user.value = user
        }
    }

    override suspend fun logout() {
        TokenStorage.token = null
        TokenStorage.userId = null
        TokenStorage.role = null
        _user.value = null
    }
}
