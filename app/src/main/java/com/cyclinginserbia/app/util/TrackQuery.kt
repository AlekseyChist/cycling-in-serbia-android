package com.cyclinginserbia.app.util

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Track

object TrackQuery {
    /**
     * Tracks for the list screen: ONLY published tracks (isPublished == true),
     * ordered by sortOrder ascending; ties broken by name, case-insensitive, ascending.
     */
    fun forDisplay(tracks: List<Track>): List<Track> {
        return tracks
            .filter { it.isPublished }
            .sortedWith(
                compareBy<Track> { it.sortOrder }
                    .thenBy { it.name.lowercase() }
            )
    }

    /**
     * Distinct region names among PUBLISHED tracks only, sorted alphabetically
     * case-insensitive. No duplicates.
     */
    fun regions(tracks: List<Track>): List<String> {
        return tracks
            .filter { it.isPublished }
            .map { it.region }
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
    }

    /**
     * Published tracks whose difficulty is contained in `difficulties`, returned in
     * forDisplay() order. If `difficulties` is EMPTY, return ALL published tracks
     * (still in forDisplay() order). Reuse forDisplay() — do not duplicate the
     * filter/sort logic.
     */
    fun byDifficulties(tracks: List<Track>, difficulties: Set<Difficulty>): List<Track> {
        val filteredTracks = if (difficulties.isEmpty()) {
            tracks.filter { it.isPublished }
        } else {
            tracks.filter { it.isPublished && it.difficulty in difficulties }
        }
        
        return forDisplay(filteredTracks)
    }
}
