package com.example.kit_market.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val phone: String)

@Serializable
data class VerifyCodeRequest(val phone: String, val code: String)

@Serializable
data class AuthResponse(val token: String)
