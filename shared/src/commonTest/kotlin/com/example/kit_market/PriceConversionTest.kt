package com.example.kit_market

import kotlin.test.Test
import kotlin.test.assertEquals

class PriceConversionTest {

    // Воспроизводим логику конвертации из OrderRepositoryImpl и ProductRepositoryImpl:
    // price (Long в копейках) → Double в рублях через toDouble() / 100.0

    private fun convertPrice(kopecks: Long): Double = kopecks.toDouble() / 100.0

    @Test
    fun wholeRubleAmount() {
        assertEquals(100.0, convertPrice(10000))
    }

    @Test
    fun amountWithKopecks() {
        assertEquals(99.99, convertPrice(9999))
    }

    @Test
    fun zeroPrice() {
        assertEquals(0.0, convertPrice(0))
    }

    @Test
    fun oneKopeck() {
        assertEquals(0.01, convertPrice(1))
    }

    @Test
    fun largeAmount() {
        assertEquals(123456.78, convertPrice(12345678))
    }

    @Test
    fun fiftyKopecks() {
        assertEquals(0.5, convertPrice(50))
    }
}
