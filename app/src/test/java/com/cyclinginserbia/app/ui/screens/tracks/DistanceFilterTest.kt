package com.cyclinginserbia.app.ui.screens.tracks

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DistanceFilterTest {

    private fun track(distanceKm: Double): Track = Track(
        uuid = "uuid-$distanceKm",
        legacyId = "legacy-$distanceKm",
        name = "T",
        region = "R",
        distanceKm = distanceKm,
        elevationM = 0,
        difficulty = Difficulty.easy,
        surface = Surface.road,
        thumbnailUrl = "",
        coordinates = null,
        description = null,
        safetyNotes = null,
        estimatedTime = null,
        photos = emptyList(),
        startPoint = null,
        endPoint = null,
        gpxFileName = null,
        route = emptyList(),
        isPublished = true,
        sortOrder = 0,
    )

    @Test
    fun `a null range accepts every distance`() {
        assertTrue(distanceInRange(0.0, null))
        assertTrue(distanceInRange(42.0, null))
        assertTrue(distanceInRange(500.0, null))
    }

    @Test
    fun `both ends of the window are inclusive`() {
        val range = 10f..30f
        assertTrue(distanceInRange(10.0, range))
        assertTrue(distanceInRange(30.0, range))
        assertTrue(distanceInRange(20.0, range))
    }

    @Test
    fun `distances outside the window are rejected`() {
        val range = 10f..30f
        assertFalse(distanceInRange(9.9, range))
        assertFalse(distanceInRange(30.1, range))
    }

    @Test
    fun `bounds start at zero and round the longest track up`() {
        val bounds = distanceBoundsKm(listOf(track(12.3), track(87.4), track(5.0)))
        assertEquals(0f, bounds.start, 0.001f)
        assertEquals(88f, bounds.endInclusive, 0.001f)
    }

    @Test
    fun `the longest track stays selectable at the top bound`() {
        val tracks = listOf(track(87.4))
        val bounds = distanceBoundsKm(tracks)
        assertTrue(distanceInRange(87.4, bounds))
    }

    @Test
    fun `an empty list still yields a usable slider range`() {
        val bounds = distanceBoundsKm(emptyList())
        assertEquals(0f, bounds.start, 0.001f)
        assertEquals(MIN_DISTANCE_SPAN_KM, bounds.endInclusive, 0.001f)
        assertTrue(bounds.start < bounds.endInclusive)
    }

    @Test
    fun `filtering keeps only the tracks inside the window`() {
        val tracks = listOf(track(5.0), track(15.0), track(25.0), track(45.0))
        val visible = tracks.applyTrackFilters(
            query = "",
            difficulty = DifficultyFilter.ALL,
            surface = SurfaceFilter.ALL,
            distanceKm = 10f..30f,
            rideType = RideTypeFilter.ALL,
            region = null,
            favoritesOnly = false,
            favoriteIds = emptySet(),
        )
        assertEquals(listOf(15.0, 25.0), visible.map { it.distanceKm })
    }
}
