package com.cyclinginserbia.app.util

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Track
import kotlin.math.roundToInt

/**
 * Client-side cycling time estimate. We compute locally instead of trusting
 * Supabase's `estimated_time` because that field is systematically wrong on
 * many tracks (audit found ~10% of rows with values like "11h 00min" for a
 * 20 km easy ride). Formula = flat-time-by-difficulty + climb minutes.
 */
object RouteEstimation {

    private val speedKmh = mapOf(
        Difficulty.easy to 18.0,
        Difficulty.medium to 15.0,
        Difficulty.hard to 12.0,
    )

    private const val MINUTES_PER_100M_CLIMB = 10

    fun estimateMinutes(distanceKm: Double, elevationM: Int, difficulty: Difficulty): Int {
        val speed = speedKmh[difficulty] ?: 15.0
        val flatMinutes = (distanceKm / speed) * 60.0
        val climbMinutes = (elevationM / 100.0) * MINUTES_PER_100M_CLIMB
        return (flatMinutes + climbMinutes).roundToInt().coerceAtLeast(1)
    }

    fun formatHM(minutes: Int): String {
        if (minutes < 60) return "${minutes}m"
        val h = minutes / 60
        val m = minutes % 60
        return if (m == 0) "${h}h" else "${h}h ${m}m"
    }

    fun displayTime(track: Track): String =
        formatHM(estimateMinutes(track.distanceKm, track.elevationM, track.difficulty))
}
