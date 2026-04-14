package com.example.kit_market.domain.repository

import com.example.kit_market.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun getCurrentUser(): User?
    suspend fun login(phone: String)
    suspend fun verifyCode(code: String): Boolean
    suspend fun updateUser(user: User)
    suspend fun logout()
}
