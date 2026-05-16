package com.example.kit_market.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.data.remote.TokenStorage
import com.example.kit_market.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Loading : AuthState()
    data object NotLoggedIn : AuthState()
    data class LoggedIn(val isWorker: Boolean) : AuthState()
}

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            if (TokenStorage.token != null) {
                try {
                    val user = userRepository.getCurrentUser()
                    if (user != null) {
                        _authState.value = AuthState.LoggedIn(isWorker = user.isWorker)
                    } else {
                        _authState.value = AuthState.NotLoggedIn
                    }
                } catch (_: Exception) {
                    TokenStorage.token = null
                    TokenStorage.userId = null
                    TokenStorage.role = null
                    _authState.value = AuthState.NotLoggedIn
                }
            } else {
                _authState.value = AuthState.NotLoggedIn
            }
        }
    }
}
