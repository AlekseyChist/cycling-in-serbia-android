package com.cyclinginserbia.app.data.supabase

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.GeoPoint
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoPointDto(val lat: Double, val lng: Double) {
    fun toModel() = GeoPoint(lat, lng)
}

@Serializable
data class TrackDto(
    val id: String,
    @SerialName("legacy_id") val legacyId: String? = null,
    val name: String,
    val region: String,
    @SerialName("distance_km") val distanceKm: Double,
    @SerialName("elevation_m") val elevationM: Int,
    val difficulty: String,
    val surface: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    val coordinates: GeoPointDto? = null,
    val description: String? = null,
    @SerialName("safety_notes") val safetyNotes: String? = null,
    @SerialName("estimated_time") val estimatedTime: String? = null,
    val photos: List<String>? = null,
    @SerialName("start_point") val startPoint: GeoPointDto? = null,
    @SerialName("end_point") val endPoint: GeoPointDto? = null,
    @SerialName("gpx_file_name") val gpxFileName: String? = null,
    @SerialName("route_points") val routePoints: List<GeoPointDto>? = null,
    @SerialName("is_published") val isPublished: Boolean,
    @SerialName("sort_order") val sortOrder: Int,
) {
    fun toModel(): Track = Track(
        uuid = id,
        legacyId = legacyId ?: id,
        name = name,
        region = region,
        distanceKm = distanceKm,
        elevationM = elevationM,
        difficulty = runCatching { Difficulty.valueOf(difficulty) }.getOrDefault(Difficulty.medium),
        surface = runCatching { Surface.valueOf(surface) }.getOrDefault(Surface.mixed),
        thumbnailUrl = thumbnailUrl.orEmpty(),
        coordinates = coordinates?.toModel(),
        description = description,
        safetyNotes = safetyNotes,
        estimatedTime = estimatedTime,
        photos = photos.orEmpty(),
        startPoint = startPoint?.toModel(),
        endPoint = endPoint?.toModel(),
        gpxFileName = gpxFileName,
        route = routePoints?.map { it.toModel() }.orEmpty(),
        isPublished = isPublished,
        sortOrder = sortOrder,
    )
}
