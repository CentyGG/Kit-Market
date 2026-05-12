package com.example.kit_market.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.usecase.LoginUseCase
import com.example.kit_market.domain.usecase.VerifyCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase
) : ViewModel() {

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
        if (phone.length != 10) {
            _state.update { it.copy(error = "Введите 10 цифр номера") }
            return
        }
        val fullPhone = "+7$phone"
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                loginUseCase(fullPhone)
                _state.update { it.copy(isLoading = false, step = AuthStep.CODE_INPUT, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Ошибка подключения к серверу") }
            }
        }
    }

    private fun submitCode() {
        val code = _state.value.code
        if (code.length != 4) {
            _state.update { it.copy(error = "Код должен состоять из 4 символов") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val success = verifyCodeUseCase(code)
                if (success) {
                    _navigateToMain.value = true
                } else {
                    _state.update { it.copy(isLoading = false, error = "Неверный код") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Ошибка подключения к серверу") }
            }
        }
    }
}
