package com.example.kit_market.presentation.screen.auth

data class AuthState(
    val step: AuthStep = AuthStep.PHONE_INPUT,
    val phone: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class AuthStep {
    PHONE_INPUT,
    CODE_INPUT
}
