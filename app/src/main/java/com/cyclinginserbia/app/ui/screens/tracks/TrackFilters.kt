package com.cyclinginserbia.app.ui.screens.tracks

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track

enum class DifficultyFilter(val label: String) {
    ALL("All"),
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
}

enum class SurfaceFilter(val label: String) {
    ALL("All Surfaces"),
    ROAD("Road"),
    GRAVEL("Gravel"),
    MIXED("Mixed"),
}

enum class RideType { COFFEE, DARK, SUN, PLUS, MISC }

enum class RideTypeFilter(val label: String) {
    ALL("All Rides"),
    COFFEE("Coffee"),
    DARK("Dark"),
    SUN("Sun"),
    PLUS("DBB+"),
    MISC("Misc"),
}

/**
 * Derives a track's ride type from its name. Mirrors the web app's
 * `getRideType` heuristic in `src/app/screens/TracksScreen.tsx`.
 */
internal fun rideTypeOf(name: String): RideType {
    val upper = name.uppercase()
    return when {
        "DBB CR" in name || "COFFEE" in upper -> RideType.COFFEE
        "DBB DOD" in name || "DBB DOR" in name || "DARK" in upper -> RideType.DARK
        "DBB SUN" in upper -> RideType.SUN
        "DBB+" in name -> RideType.PLUS
        else -> RideType.MISC
    }
}

internal fun List<Track>.applyTrackFilters(
    query: String,
    difficulty: DifficultyFilter,
    surface: SurfaceFilter,
    rideType: RideTypeFilter,
    region: String?,
    favoritesOnly: Boolean,
    favoriteIds: Set<String>,
): List<Track> {
    val q = query.trim()
    return asSequence()
        .filter { track ->
            when (difficulty) {
                DifficultyFilter.ALL -> true
                DifficultyFilter.EASY -> track.difficulty == Difficulty.easy
                DifficultyFilter.MEDIUM -> track.difficulty == Difficulty.medium
                DifficultyFilter.HARD -> track.difficulty == Difficulty.hard
            }
        }
        .filter { track ->
            when (surface) {
                SurfaceFilter.ALL -> true
                SurfaceFilter.ROAD -> track.surface == Surface.road
                SurfaceFilter.GRAVEL -> track.surface == Surface.gravel
                SurfaceFilter.MIXED -> track.surface == Surface.mixed
            }
        }
        .filter { track ->
            when (rideType) {
                RideTypeFilter.ALL -> true
                RideTypeFilter.COFFEE -> rideTypeOf(track.name) == RideType.COFFEE
                RideTypeFilter.DARK -> rideTypeOf(track.name) == RideType.DARK
                RideTypeFilter.SUN -> rideTypeOf(track.name) == RideType.SUN
                RideTypeFilter.PLUS -> rideTypeOf(track.name) == RideType.PLUS
                RideTypeFilter.MISC -> rideTypeOf(track.name) == RideType.MISC
            }
        }
        .filter { track -> region == null || track.region == region }
        .filter { track -> !favoritesOnly || track.uuid in favoriteIds }
        .filter { track ->
            q.isEmpty() ||
                track.name.contains(q, ignoreCase = true) ||
                track.region.contains(q, ignoreCase = true)
        }
        .toList()
}
