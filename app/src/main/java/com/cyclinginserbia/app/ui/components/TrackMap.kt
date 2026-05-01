package com.cyclinginserbia.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.theme.DifficultyEasy
import com.cyclinginserbia.app.ui.theme.DifficultyHard
import com.cyclinginserbia.app.ui.theme.DifficultyMedium
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * AndroidView wrapper around osmdroid MapView. Renders the track route as a polyline
 * coloured by difficulty, plus a start marker. Uses OpenStreetMap raster tiles —
 * the same provider as the web app's Leaflet map.
 */
@Composable
fun TrackMap(track: Track, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }

    DisposableEffect(track.uuid) {
        mapView.overlays.clear()

        val routePoints = track.route.map { GeoPoint(it.lat, it.lng) }

        if (routePoints.isNotEmpty()) {
            val poly = Polyline().apply {
                setPoints(routePoints)
                outlinePaint.color = colorForDifficulty(track.difficulty)
                outlinePaint.strokeWidth = 8f
            }
            mapView.overlays.add(poly)

            val start = track.startPoint?.let { GeoPoint(it.lat, it.lng) } ?: routePoints.first()
            val marker = Marker(mapView).apply {
                position = start
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = track.name
            }
            mapView.overlays.add(marker)

            mapView.post {
                val bounds = org.osmdroid.util.BoundingBox.fromGeoPointsSafe(routePoints)
                mapView.zoomToBoundingBox(bounds, false, 64)
            }
        } else {
            val center = track.coordinates?.let { GeoPoint(it.lat, it.lng) }
                ?: GeoPoint(44.0165, 21.0059) // Serbia center
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(center)
        }

        mapView.invalidate()
        onDispose { /* MapView is reused via remember; nothing to dispose here */ }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

private fun colorForDifficulty(d: Difficulty): Int = when (d) {
    Difficulty.easy -> DifficultyEasy.toArgb()
    Difficulty.medium -> DifficultyMedium.toArgb()
    Difficulty.hard -> DifficultyHard.toArgb()
}
