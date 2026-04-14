package com.example.kit_market.domain.usecase

import com.example.kit_market.domain.repository.UserRepository

class VerifyCodeUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(code: String): Boolean = repository.verifyCode(code)
}
