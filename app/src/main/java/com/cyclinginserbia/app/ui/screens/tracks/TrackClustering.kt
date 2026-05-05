package com.cyclinginserbia.app.ui.screens.tracks

import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.GeoPoint
import com.cyclinginserbia.app.data.model.Track
import kotlin.math.abs

private const val PROXIMITY_THRESHOLD_DEG = 0.002

data class TrackCluster(
    val position: GeoPoint,
    val tracks: List<Track>,
    val primaryDifficulty: Difficulty,
)

internal fun groupTracksByStartPoint(tracks: List<Track>): List<TrackCluster> {
    data class Acc(var position: GeoPoint, val tracks: MutableList<Track> = mutableListOf())

    val groups = mutableListOf<Acc>()

    for (track in tracks) {
        val pos = track.startPoint ?: track.route.firstOrNull() ?: track.coordinates ?: continue
        val match = groups.firstOrNull { group ->
            abs(group.position.lat - pos.lat) < PROXIMITY_THRESHOLD_DEG &&
                abs(group.position.lng - pos.lng) < PROXIMITY_THRESHOLD_DEG
        }
        if (match != null) {
            match.tracks.add(track)
        } else {
            groups.add(Acc(position = pos, tracks = mutableListOf(track)))
        }
    }

    return groups.map { acc ->
        TrackCluster(
            position = acc.position,
            tracks = acc.tracks.toList(),
            primaryDifficulty = acc.tracks.maxBy { it.difficulty.rank }.difficulty,
        )
    }
}

private val Difficulty.rank: Int
    get() = when (this) {
        Difficulty.easy -> 0
        Difficulty.medium -> 1
        Difficulty.hard -> 2
    }
