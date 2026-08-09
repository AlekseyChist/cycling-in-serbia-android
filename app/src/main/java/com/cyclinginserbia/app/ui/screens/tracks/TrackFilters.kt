package com.cyclinginserbia.app.ui.screens.tracks

import androidx.annotation.StringRes
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.util.containsFolded
import kotlin.math.ceil

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

/** Smallest distance window the slider may be squeezed to, in km. */
const val MIN_DISTANCE_SPAN_KM = 1f

/**
 * Slider bounds for the distance filter: always starts at 0 and ends at the
 * longest track, rounded up so that track stays selectable at the top end.
 * Falls back to 0..[MIN_DISTANCE_SPAN_KM] while the list is still empty, since a
 * RangeSlider cannot take an empty range.
 */
fun distanceBoundsKm(tracks: List<Track>): ClosedFloatingPointRange<Float> {
    val longest = tracks.maxOfOrNull { it.distanceKm } ?: 0.0
    return 0f..ceil(longest).toFloat().coerceAtLeast(MIN_DISTANCE_SPAN_KM)
}

/**
 * True when [distanceKm] falls inside the selected window. Both ends are
 * inclusive: the slider is a user-facing "from X to Y", so a track sitting
 * exactly on a handle must stay visible.
 */
fun distanceInRange(distanceKm: Double, range: ClosedFloatingPointRange<Float>?): Boolean =
    range == null || distanceKm.toFloat() in range

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
    distanceKm: ClosedFloatingPointRange<Float>?,
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
        .filter { track -> distanceInRange(track.distanceKm, distanceKm) }
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
