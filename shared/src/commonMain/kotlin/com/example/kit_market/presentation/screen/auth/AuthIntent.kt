package com.example.kit_market.presentation.screen.auth

sealed interface AuthIntent {
    data class EnterPhone(val phone: String) : AuthIntent
    data object SubmitPhone : AuthIntent
    data class EnterCode(val code: String) : AuthIntent
    data object SubmitCode : AuthIntent
}
