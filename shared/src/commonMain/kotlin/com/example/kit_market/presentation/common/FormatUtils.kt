package com.example.kit_market.presentation.common

import kotlin.math.abs
import kotlin.math.roundToLong

fun Double.formatPrice(): String {
    val rounded = (this * 100).roundToLong()
    val intPart = rounded / 100
    val decPart = abs(rounded % 100)
    return "$intPart.${decPart.toString().padStart(2, '0')}"
}
