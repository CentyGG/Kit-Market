package com.example.kit_market.domain.model

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val categoryId: Long
)
