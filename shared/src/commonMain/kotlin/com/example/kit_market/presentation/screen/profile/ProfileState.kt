package com.example.kit_market.presentation.screen.profile

import com.example.kit_market.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isEditing: Boolean = false,
    val editFirstName: String = "",
    val editLastName: String = ""
)
