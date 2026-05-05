package com.cyclinginserbia.app.ui.screens.tracks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.theme.DifficultyColors
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private val SERBIA_CENTER = GeoPoint(44.0165, 21.0059)
private const val DEFAULT_ZOOM = 7.0

@Composable
fun TracksMapView(
    tracks: List<Track>,
    selectedId: String?,
    onClusterClick: (TrackCluster) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(SERBIA_CENTER)
            isVerticalMapRepetitionEnabled = false
            isHorizontalMapRepetitionEnabled = false
        }
    }

    DisposableEffect(tracks) {
        mapView.overlays.clear()

        for (track in tracks) {
            if (track.route.isEmpty()) continue
            val points = track.route.map { GeoPoint(it.lat, it.lng) }
            val isSelected = track.uuid == selectedId
            val poly = Polyline().apply {
                setPoints(points)
                outlinePaint.color = colorForDifficulty(track.difficulty)
                outlinePaint.strokeWidth = if (isSelected) 8f else 5f
                outlinePaint.alpha = if (isSelected) 255 else 190
            }
            mapView.overlays.add(poly)
        }

        val clusters = groupTracksByStartPoint(tracks)
        for (cluster in clusters) {
            val isSelected = cluster.tracks.any { it.uuid == selectedId }
            val marker = Marker(mapView).apply {
                position = GeoPoint(cluster.position.lat, cluster.position.lng)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = buildClusterMarker(
                    context = context,
                    difficulty = cluster.primaryDifficulty,
                    count = cluster.tracks.size,
                    isSelected = isSelected,
                )
                title = if (cluster.tracks.size == 1) cluster.tracks.first().name
                else "${cluster.tracks.size} routes from here"
                setOnMarkerClickListener { _, _ ->
                    onClusterClick(cluster)
                    true
                }
            }
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
        onDispose { /* MapView reused via remember */ }
    }

    DisposableEffect(selectedId) {
        if (selectedId != null) {
            val track = tracks.firstOrNull { it.uuid == selectedId }
            val points = track?.route?.map { GeoPoint(it.lat, it.lng) }.orEmpty()
            if (points.isNotEmpty()) {
                mapView.post {
                    val bounds = BoundingBox.fromGeoPointsSafe(points)
                    mapView.zoomToBoundingBox(bounds, true, 96)
                }
            }
        } else {
            val allPoints = tracks.flatMap { t -> t.route.map { GeoPoint(it.lat, it.lng) } }
            if (allPoints.isNotEmpty()) {
                mapView.post {
                    val bounds = BoundingBox.fromGeoPointsSafe(allPoints)
                    mapView.zoomToBoundingBox(bounds, true, 64)
                }
            }
        }
        onDispose { }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

private fun colorForDifficulty(d: Difficulty): Int = when (d) {
    Difficulty.easy -> DifficultyColors.Easy.toArgb()
    Difficulty.medium -> DifficultyColors.Medium.toArgb()
    Difficulty.hard -> DifficultyColors.Hard.toArgb()
}
