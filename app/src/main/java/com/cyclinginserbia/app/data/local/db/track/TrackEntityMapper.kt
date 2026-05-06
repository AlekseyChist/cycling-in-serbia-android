package com.cyclinginserbia.app.data.local.db.track

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.supabase.TrackDto

fun TrackDto.toEntity(): TrackEntity = TrackEntity(
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

fun TrackEntity.toModel(): Track = Track(
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
    sortOrder = sortOrder,
)
