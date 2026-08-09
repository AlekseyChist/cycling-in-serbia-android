package com.cyclinginserbia.app.util

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Track
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackQueryTest {
    
    private fun track(
        name: String = "T",
        region: String = "R",
        difficulty: Difficulty = Difficulty.easy,
        sortOrder: Int = 0,
        isPublished: Boolean = true,
        uuid: String = "uuid",
        legacyId: String = "legacyId",
        distanceKm: Double = 1.0,
        elevationM: Int = 100,
        surface: com.cyclinginserbia.app.data.model.Surface = com.cyclinginserbia.app.data.model.Surface.road,
        thumbnailUrl: String = "thumbnailUrl",
        coordinates: com.cyclinginserbia.app.data.model.GeoPoint? = null,
        description: String? = null,
        safetyNotes: String? = null,
        estimatedTime: String? = null,
        photos: List<String> = emptyList(),
        startPoint: com.cyclinginserbia.app.data.model.GeoPoint? = null,
        endPoint: com.cyclinginserbia.app.data.model.GeoPoint? = null,
        gpxFileName: String? = null,
        route: List<com.cyclinginserbia.app.data.model.GeoPoint> = emptyList(),
    ): Track = Track(
        uuid = uuid,
        legacyId = legacyId,
        name = name,
        region = region,
        distanceKm = distanceKm,
        elevationM = elevationM,
        difficulty = difficulty,
        surface = surface,
        thumbnailUrl = thumbnailUrl,
        coordinates = coordinates,
        description = description,
        safetyNotes = safetyNotes,
        estimatedTime = estimatedTime,
        photos = photos,
        startPoint = startPoint,
        endPoint = endPoint,
        gpxFileName = gpxFileName,
        route = route,
        isPublished = isPublished,
        sortOrder = sortOrder
    )

    @Test
    fun forDisplay_filters_out_isPublished_false() {
        val tracks = listOf(
            track(name = "A", isPublished = true),
            track(name = "B", isPublished = false),
            track(name = "C", isPublished = true)
        )
        
        val result = TrackQuery.forDisplay(tracks)
        assertEquals(2, result.size)
        assertEquals("A", result[0].name)
        assertEquals("C", result[1].name)
    }

    @Test
    fun forDisplay_orders_by_sortOrder_ascending() {
        val tracks = listOf(
            track(name = "B", sortOrder = 2, isPublished = true),
            track(name = "A", sortOrder = 1, isPublished = true),
            track(name = "C", sortOrder = 3, isPublished = true)
        )
        
        val result = TrackQuery.forDisplay(tracks)
        assertEquals(3, result.size)
        assertEquals("A", result[0].name)
        assertEquals("B", result[1].name)
        assertEquals("C", result[2].name)
    }

    @Test
    fun forDisplay_breaks_sortOrder_ties_by_name_case_insensitive() {
        val tracks = listOf(
            track(name = "banana", sortOrder = 1, isPublished = true),
            track(name = "Apple", sortOrder = 1, isPublished = true),
            track(name = "cherry", sortOrder = 1, isPublished = true)
        )
        
        val result = TrackQuery.forDisplay(tracks)
        assertEquals(3, result.size)
        assertEquals("Apple", result[0].name)
        assertEquals("banana", result[1].name)
        assertEquals("cherry", result[2].name)
    }

    @Test
    fun regions_returns_distinct_region_names_of_published_tracks_alphabetically() {
        val tracks = listOf(
            track(region = "Z", isPublished = true),
            track(region = "a", isPublished = true),
            track(region = "B", isPublished = true),
            track(region = "a", isPublished = false), // unpublished - should be excluded
            track(region = "Z", isPublished = true)   // duplicate - should be excluded
        )
        
        val result = TrackQuery.regions(tracks)
        assertEquals(3, result.size)
        assertEquals("a", result[0])
        assertEquals("B", result[1])
        assertEquals("Z", result[2])
    }

    @Test
    fun byDifficulties_with_non_empty_set_returns_only_matching_published_tracks_in_forDisplay_order() {
        val tracks = listOf(
            track(name = "C", difficulty = Difficulty.hard, sortOrder = 3, isPublished = true),
            track(name = "A", difficulty = Difficulty.easy, sortOrder = 1, isPublished = true),
            track(name = "B", difficulty = Difficulty.medium, sortOrder = 2, isPublished = true),
            track(name = "D", difficulty = Difficulty.hard, sortOrder = 4, isPublished = false) // unpublished - should be excluded
        )
        
        val result = TrackQuery.byDifficulties(tracks, setOf(Difficulty.easy, Difficulty.hard))
        assertEquals(2, result.size)
        assertEquals("A", result[0].name)
        assertEquals("C", result[1].name)
    }

    @Test
    fun byDifficulties_with_empty_set_returns_all_published_tracks_in_forDisplay_order() {
        val tracks = listOf(
            track(name = "C", difficulty = Difficulty.hard, sortOrder = 3, isPublished = true),
            track(name = "A", difficulty = Difficulty.easy, sortOrder = 1, isPublished = true),
            track(name = "B", difficulty = Difficulty.medium, sortOrder = 2, isPublished = true),
            track(name = "D", difficulty = Difficulty.hard, sortOrder = 4, isPublished = false) // unpublished - should be excluded
        )
        
        val result = TrackQuery.byDifficulties(tracks, emptySet())
        assertEquals(3, result.size)
        assertEquals("A", result[0].name)
        assertEquals("B", result[1].name)
        assertEquals("C", result[2].name)
    }
}
