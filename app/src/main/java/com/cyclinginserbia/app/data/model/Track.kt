package com.cyclinginserbia.app.data.model

enum class Difficulty { easy, medium, hard }
enum class Surface { road, gravel, mixed }

data class GeoPoint(val lat: Double, val lng: Double)

data class Track(
    val uuid: String,
    val legacyId: String,
    val name: String,
    val region: String,
    val distanceKm: Double,
    val elevationM: Int,
    val difficulty: Difficulty,
    val surface: Surface,
    val thumbnailUrl: String,
    val coordinates: GeoPoint?,
    val description: String?,
    val safetyNotes: String?,
    val estimatedTime: String?,
    val photos: List<String>,
    val startPoint: GeoPoint?,
    val endPoint: GeoPoint?,
    val gpxFileName: String?,
    val route: List<GeoPoint>,
    val isPublished: Boolean,
    val sortOrder: Int,
)
