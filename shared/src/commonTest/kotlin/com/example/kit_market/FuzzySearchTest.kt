package com.example.kit_market

import com.example.kit_market.domain.util.fuzzyMatchScore
import com.example.kit_market.domain.util.levenshteinDistance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FuzzySearchTest {

    // --- levenshteinDistance ---

    @Test
    fun identicalStringsHaveZeroDistance() {
        assertEquals(0, levenshteinDistance("молоко", "молоко"))
    }

    @Test
    fun emptyAndNonEmptyStringDistanceIsLength() {
        assertEquals(5, levenshteinDistance("", "hello"))
        assertEquals(5, levenshteinDistance("hello", ""))
    }

    @Test
    fun bothEmptyStringsDistanceIsZero() {
        assertEquals(0, levenshteinDistance("", ""))
    }

    @Test
    fun singleCharacterDifference() {
        assertEquals(1, levenshteinDistance("кот", "код"))
    }

    @Test
    fun caseInsensitiveComparison() {
        assertEquals(0, levenshteinDistance("Hello", "hello"))
    }

    // --- fuzzyMatchScore ---

    @Test
    fun exactSubstringMatchReturnsZero() {
        assertEquals(0, fuzzyMatchScore("молоко", "Молоко пастеризованное"))
    }

    @Test
    fun typoInQueryStillMatches() {
        val score = fuzzyMatchScore("малоко", "молоко")
        assertNotNull(score)
    }

    @Test
    fun completelyDifferentQueryReturnsNull() {
        assertNull(fuzzyMatchScore("компьютер", "молоко"))
    }

    @Test
    fun emptyQueryReturnsNull() {
        assertNull(fuzzyMatchScore("", "молоко"))
    }

    @Test
    fun multiWordQueryMatchesAllWords() {
        val score = fuzzyMatchScore("красное яблоко", "Яблоко красное сезонное")
        assertNotNull(score)
    }

    @Test
    fun multiWordQueryFailsIfOneWordDoesNotMatch() {
        assertNull(fuzzyMatchScore("красный компьютер", "Яблоко красное сезонное"))
    }

    @Test
    fun queryMatchesCaseInsensitive() {
        assertEquals(0, fuzzyMatchScore("МОЛОКО", "молоко домашнее"))
    }
}
