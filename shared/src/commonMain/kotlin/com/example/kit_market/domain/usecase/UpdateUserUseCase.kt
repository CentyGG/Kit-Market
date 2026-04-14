package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.model.User
import com.example.kit_market.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.updateUser(user)
}
