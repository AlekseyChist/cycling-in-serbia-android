package com.cyclinginserbia.app.ui.screens.tracks

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DistanceFilterTest {

    @Test
    fun `ALL matches every distance`() {
        val distances = listOf(0.0, 5.0, 10.0, 30.0, 500.0)
        for (distance in distances) {
            assertTrue(DistanceFilter.ALL.matches(distance))
        }
    }

    @Test
    fun `SHORT covers everything below 10 km`() {
        assertTrue(DistanceFilter.SHORT.matches(0.0))
        assertTrue(DistanceFilter.SHORT.matches(9.9))
        assertFalse(DistanceFilter.SHORT.matches(10.0))
        assertFalse(DistanceFilter.SHORT.matches(30.0))
    }

    @Test
    fun `MEDIUM covers 10 km up to but not including 30 km`() {
        assertTrue(DistanceFilter.MEDIUM.matches(10.0))
        assertTrue(DistanceFilter.MEDIUM.matches(29.9))
        assertFalse(DistanceFilter.MEDIUM.matches(9.9))
        assertFalse(DistanceFilter.MEDIUM.matches(30.0))
    }

    @Test
    fun `LONG covers 30 km and above`() {
        assertTrue(DistanceFilter.LONG.matches(30.0))
        assertTrue(DistanceFilter.LONG.matches(200.0))
        assertFalse(DistanceFilter.LONG.matches(29.9))
    }

    @Test
    fun `boundary values each belong to exactly one non-ALL bucket`() {
        // 10.0 belongs to MEDIUM (not SHORT, not LONG)
        assertTrue(DistanceFilter.MEDIUM.matches(10.0))
        assertFalse(DistanceFilter.SHORT.matches(10.0))
        assertFalse(DistanceFilter.LONG.matches(10.0))

        // 30.0 belongs to LONG (not SHORT, not MEDIUM)
        assertTrue(DistanceFilter.LONG.matches(30.0))
        assertFalse(DistanceFilter.SHORT.matches(30.0))
        assertFalse(DistanceFilter.MEDIUM.matches(30.0))
    }
}
