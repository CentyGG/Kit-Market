package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.User
import com.example.kit_market.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<User?> = repository.getUser()
}
