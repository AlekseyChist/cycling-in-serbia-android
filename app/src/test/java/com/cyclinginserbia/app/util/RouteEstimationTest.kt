package com.cyclinginserbia.app.util

import com.cyclinginserbia.app.data.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Test

class RouteEstimationTest {

    @Test
    fun estimateMinutes_flatEasy() {
        // 18 km easy, 0 m elevation -> 60 min
        val result = RouteEstimation.estimateMinutes(18.0, 0, Difficulty.easy)
        assertEquals(60, result)
    }

    @Test
    fun estimateMinutes_difficultyChange() {
        // 15 km, same distance, easy vs hard
        // easy: 15 / 18 * 60 = 50 min
        // hard: 15 / 12 * 60 = 75 min
        val resultEasy = RouteEstimation.estimateMinutes(15.0, 0, Difficulty.easy)
        val resultHard = RouteEstimation.estimateMinutes(15.0, 0, Difficulty.hard)
        assertEquals(50, resultEasy)
        assertEquals(75, resultHard)
    }

    @Test
    fun estimateMinutes_climbContribution() {
        // 0 km distance but 300 m elevation -> 30 min
        // climb: 300 / 100 * 10 = 30 min
        val result = RouteEstimation.estimateMinutes(0.0, 300, Difficulty.easy)
        assertEquals(30, result)
    }

    @Test
    fun estimateMinutes_lowerBound() {
        // 0 km, 0 m -> 1
        val result = RouteEstimation.estimateMinutes(0.0, 0, Difficulty.easy)
        assertEquals(1, result)
    }

    @Test
    fun formatHM_minutes() {
        // 42 min -> "42m"
        val result = RouteEstimation.formatHM(42)
        assertEquals("42m", result)
    }

    @Test
    fun formatHM_hours() {
        // 120 min -> "2h"
        val result = RouteEstimation.formatHM(120)
        assertEquals("2h", result)
    }

    @Test
    fun formatHM_hoursAndMinutes() {
        // 90 min -> "1h 30m"
        val result = RouteEstimation.formatHM(90)
        assertEquals("1h 30m", result)
    }
}
