package com.example.kit_market.presentation.screen.profile

sealed interface ProfileIntent {
    data object ToggleEdit : ProfileIntent
    data class UpdateFirstName(val name: String) : ProfileIntent
    data class UpdateLastName(val name: String) : ProfileIntent
    data object SaveName : ProfileIntent
    data object Logout : ProfileIntent
}
