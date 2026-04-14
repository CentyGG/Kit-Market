package com.example.kit_market.presentation.screen.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.usecase.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenModel(
    private val getUserUseCase: GetUserUseCase
) : ScreenModel {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        screenModelScope.launch {
            kotlinx.coroutines.delay(1500)
            val user = getUserUseCase().first()
            _isLoggedIn.value = user != null
        }
    }
}
