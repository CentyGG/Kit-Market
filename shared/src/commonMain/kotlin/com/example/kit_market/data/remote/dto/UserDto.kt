package com.example.kit_market.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val id: Long, val name: String, val phone: String)

@Serializable
data class UpdateUserRequest(val name: String)
