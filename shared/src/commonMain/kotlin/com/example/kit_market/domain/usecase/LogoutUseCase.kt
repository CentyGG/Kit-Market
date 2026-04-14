package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.UserRepository

class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.logout()
}
