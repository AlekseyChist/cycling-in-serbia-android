package com.cyclinginserbia.app.data.local.db

import androidx.room.TypeConverter
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.GeoPoint
import com.cyclinginserbia.app.data.model.Surface
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun difficultyToString(value: Difficulty): String = value.name

    @TypeConverter
    fun stringToDifficulty(value: String): Difficulty =
        runCatching { Difficulty.valueOf(value) }.getOrDefault(Difficulty.medium)

    @TypeConverter
    fun surfaceToString(value: Surface): String = value.name

    @TypeConverter
    fun stringToSurface(value: String): Surface =
        runCatching { Surface.valueOf(value) }.getOrDefault(Surface.mixed)

    @TypeConverter
    fun geoPointToString(value: GeoPoint?): String? =
        value?.let { json.encodeToString(GeoPointSurrogate.serializer(), it.toSurrogate()) }

    @TypeConverter
    fun stringToGeoPoint(value: String?): GeoPoint? =
        value?.takeIf { it.isNotBlank() }
            ?.let { json.decodeFromString(GeoPointSurrogate.serializer(), it).toModel() }

    @TypeConverter
    fun stringListToString(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun stringToStringList(value: String): List<String> =
        if (value.isBlank()) emptyList()
        else json.decodeFromString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun geoPointListToString(value: List<GeoPoint>): String =
        json.encodeToString(
            ListSerializer(GeoPointSurrogate.serializer()),
            value.map { it.toSurrogate() },
        )

    @TypeConverter
    fun stringToGeoPointList(value: String): List<GeoPoint> {
        if (value.isBlank()) return emptyList()
        return json.decodeFromString(ListSerializer(GeoPointSurrogate.serializer()), value)
            .map { it.toModel() }
    }
}

@Serializable
private data class GeoPointSurrogate(val lat: Double, val lng: Double)

private fun GeoPoint.toSurrogate() = GeoPointSurrogate(lat, lng)
private fun GeoPointSurrogate.toModel() = GeoPoint(lat, lng)
