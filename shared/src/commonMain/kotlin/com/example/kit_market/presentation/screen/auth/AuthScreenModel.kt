package com.example.kit_market.presentation.screen.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.usecase.LoginUseCase
import com.example.kit_market.domain.usecase.VerifyCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthScreenModel(
    private val loginUseCase: LoginUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain = _navigateToMain.asStateFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EnterPhone -> {
                _state.update { it.copy(phone = intent.phone, error = null) }
            }
            is AuthIntent.SubmitPhone -> submitPhone()
            is AuthIntent.EnterCode -> {
                _state.update { it.copy(code = intent.code, error = null) }
            }
            is AuthIntent.SubmitCode -> submitCode()
        }
    }

    private fun submitPhone() {
        val phone = _state.value.phone
        if (phone.isBlank()) {
            _state.update { it.copy(error = "Введите номер телефона") }
            return
        }
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loginUseCase(phone)
            _state.update { it.copy(isLoading = false, step = AuthStep.CODE_INPUT, error = null) }
        }
    }

    private fun submitCode() {
        val code = _state.value.code
        if (code.length != 6) {
            _state.update { it.copy(error = "Код должен состоять из 6 символов") }
            return
        }
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val success = verifyCodeUseCase(code)
            if (success) {
                _navigateToMain.value = true
            } else {
                _state.update { it.copy(isLoading = false, error = "Неверный код") }
            }
        }
    }
}
