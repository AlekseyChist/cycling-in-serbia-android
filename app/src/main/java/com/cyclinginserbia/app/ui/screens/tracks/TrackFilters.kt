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

internal fun List<Track>.applyTrackFilters(
    query: String,
    difficulty: DifficultyFilter,
    surface: SurfaceFilter,
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
            q.isEmpty() ||
                track.name.contains(q, ignoreCase = true) ||
                track.region.contains(q, ignoreCase = true)
        }
        .toList()
}
