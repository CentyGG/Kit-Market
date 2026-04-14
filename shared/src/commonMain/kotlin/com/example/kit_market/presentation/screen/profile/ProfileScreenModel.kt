package com.example.kit_market.presentation.screen.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.kit_market.domain.usecase.GetUserUseCase
import com.example.kit_market.domain.usecase.LogoutUseCase
import com.example.kit_market.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut = _loggedOut.asStateFlow()

    init {
        observeUser()
    }

    private fun observeUser() {
        screenModelScope.launch {
            getUserUseCase().collect { user ->
                _state.update {
                    it.copy(
                        user = user,
                        editFirstName = user?.firstName ?: "",
                        editLastName = user?.lastName ?: ""
                    )
                }
            }
        }
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
                screenModelScope.launch {
                    updateUserUseCase(updated)
                    _state.update { it.copy(isEditing = false) }
                }
            }
            ProfileIntent.Logout -> {
                screenModelScope.launch {
                    logoutUseCase()
                    _loggedOut.value = true
                }
            }
        }
    }
}
