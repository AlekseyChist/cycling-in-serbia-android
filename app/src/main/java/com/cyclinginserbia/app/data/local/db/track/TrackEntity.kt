package com.cyclinginserbia.app.data.local.db.track

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.GeoPoint
import com.cyclinginserbia.app.data.model.Surface

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["legacy_id"], unique = true),
        Index(value = ["is_published", "sort_order"]),
    ],
)
data class TrackEntity(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "legacy_id") val legacyId: String,
    val name: String,
    val region: String,
    @ColumnInfo(name = "distance_km") val distanceKm: Double,
    @ColumnInfo(name = "elevation_m") val elevationM: Int,
    val difficulty: Difficulty,
    val surface: Surface,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    val coordinates: GeoPoint?,
    val description: String?,
    @ColumnInfo(name = "safety_notes") val safetyNotes: String?,
    @ColumnInfo(name = "estimated_time") val estimatedTime: String?,
    val photos: List<String>,
    @ColumnInfo(name = "start_point") val startPoint: GeoPoint?,
    @ColumnInfo(name = "end_point") val endPoint: GeoPoint?,
    @ColumnInfo(name = "gpx_file_name") val gpxFileName: String?,
    val route: List<GeoPoint>,
    @ColumnInfo(name = "is_published") val isPublished: Boolean,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
)
