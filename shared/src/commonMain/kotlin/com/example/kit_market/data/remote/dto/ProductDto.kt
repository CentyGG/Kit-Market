package com.example.kit_market.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val category: String = "",
    val price: Long,
    val isActive: Boolean,
    val imageUrl: String = ""
)
