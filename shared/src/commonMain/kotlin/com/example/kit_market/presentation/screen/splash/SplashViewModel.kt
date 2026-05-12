package com.example.kit_market.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.data.remote.TokenStorage
import com.example.kit_market.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            if (TokenStorage.token != null) {
                try {
                    val user = userRepository.getCurrentUser()
                    _isLoggedIn.value = user != null
                } catch (_: Exception) {
                    TokenStorage.token = null
                    TokenStorage.userId = null
                    _isLoggedIn.value = false
                }
            } else {
                _isLoggedIn.value = false
            }
        }
    }
}
