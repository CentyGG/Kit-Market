package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(phone: String) = repository.login(phone)
}
