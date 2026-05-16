package com.example.kit_market

import com.example.kit_market.presentation.common.formatPrice
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatPriceTest {

    @Test
    fun formatsWholeNumber() {
        assertEquals("100.00", 100.0.formatPrice())
    }

    @Test
    fun formatsWithTwoDecimals() {
        assertEquals("99.99", 99.99.formatPrice())
    }

    @Test
    fun formatsWithOneDecimalPadsZero() {
        assertEquals("10.50", 10.5.formatPrice())
    }

    @Test
    fun formatsZero() {
        assertEquals("0.00", 0.0.formatPrice())
    }

    @Test
    fun formatsSmallAmount() {
        assertEquals("0.01", 0.01.formatPrice())
    }

    @Test
    fun formatsLargeAmount() {
        assertEquals("12345.67", 12345.67.formatPrice())
    }

    @Test
    fun roundsThirdDecimalDown() {
        assertEquals("1.12", 1.124.formatPrice())
    }

    @Test
    fun roundsThirdDecimalUp() {
        assertEquals("1.13", 1.125.formatPrice())
    }
}
