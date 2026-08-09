package com.cyclinginserbia.app.ui.screens.tracks

import androidx.annotation.StringRes
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.util.containsFolded

enum class DifficultyFilter(@StringRes val labelRes: Int) {
    ALL(R.string.difficulty_all),
    EASY(R.string.difficulty_easy),
    MEDIUM(R.string.difficulty_medium),
    HARD(R.string.difficulty_hard),
}

enum class SurfaceFilter(@StringRes val labelRes: Int) {
    ALL(R.string.surface_all),
    ROAD(R.string.surface_road),
    GRAVEL(R.string.surface_gravel),
    MIXED(R.string.surface_mixed),
}

enum class RideType { COFFEE, DARK, SUN, PLUS, MISC }

enum class RideTypeFilter(@StringRes val labelRes: Int) {
    ALL(R.string.ridetype_all),
    COFFEE(R.string.ridetype_coffee),
    DARK(R.string.ridetype_dark),
    SUN(R.string.ridetype_sun),
    PLUS(R.string.ridetype_plus),
    MISC(R.string.ridetype_misc),
}

/**
 * Distance buckets for the Tracks filter sheet. Ranges are half-open
 * (`minKm` inclusive, `maxKm` exclusive) so every track falls into exactly one.
 */
enum class DistanceFilter(
    @StringRes val labelRes: Int,
    private val minKm: Double,
    private val maxKm: Double,
) {
    ALL(R.string.distance_all, 0.0, Double.POSITIVE_INFINITY),
    SHORT(R.string.distance_short, 0.0, 10.0),
    MEDIUM(R.string.distance_medium, 10.0, 30.0),
    LONG(R.string.distance_long, 30.0, Double.POSITIVE_INFINITY);

    fun matches(distanceKm: Double): Boolean = distanceKm >= minKm && distanceKm < maxKm
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
    distance: DistanceFilter,
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
        .filter { track -> distance.matches(track.distanceKm) }
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
                track.name.containsFolded(q) ||
                track.region.containsFolded(q)
        }
        .toList()
}
