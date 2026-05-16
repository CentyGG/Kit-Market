package com.example.kit_market.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.domain.repository.UserRepository
import com.example.kit_market.domain.usecase.GetUserUseCase
import com.example.kit_market.domain.usecase.LogoutUseCase
import com.example.kit_market.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut = _loggedOut.asStateFlow()

    init {
        observeUser()
        loadProfile()
    }

    private fun observeUser() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _state.update {
                    it.copy(
                        user = user,
                        isLoadingProfile = false,
                        editFirstName = user?.firstName ?: "",
                        editLastName = user?.lastName ?: ""
                    )
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProfile = true, error = null) }
            try {
                userRepository.getCurrentUser()
            } catch (_: Exception) {
                _state.update {
                    it.copy(
                        isLoadingProfile = false,
                        error = if (it.user == null) "Не удалось загрузить профиль. Проверьте интернет-соединение." else null
                    )
                }
            }
        }
    }

    fun retryLoadProfile() {
        loadProfile()
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.ToggleEdit -> {
                val current = _state.value
                _state.update {
                    it.copy(
                        isEditing = !current.isEditing,
                        editFirstName = current.user?.firstName ?: "",
                        editLastName = current.user?.lastName ?: ""
                    )
                }
            }
            is ProfileIntent.UpdateFirstName -> {
                _state.update { it.copy(editFirstName = intent.name) }
            }
            is ProfileIntent.UpdateLastName -> {
                _state.update { it.copy(editLastName = intent.name) }
            }
            ProfileIntent.SaveName -> {
                val user = _state.value.user ?: return
                val updated = user.copy(
                    firstName = _state.value.editFirstName,
                    lastName = _state.value.editLastName
                )
                viewModelScope.launch {
                    _state.update { it.copy(error = null) }
                    try {
                        updateUserUseCase(updated)
                        _state.update { it.copy(isEditing = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = "Не удалось сохранить. Проверьте интернет-соединение.") }
                    }
                }
            }
            ProfileIntent.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _loggedOut.value = true
                }
            }
        }
    }
}
