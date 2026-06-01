package com.cyclinginserbia.app.ui.screens.tracks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Shop
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.theme.DifficultyMapColors
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// Belgrade as the default focus — most tracks (and the DBB community) live around the capital.
private val DEFAULT_CENTER = GeoPoint(44.7866, 20.4489)
private const val DEFAULT_ZOOM = 9.5

/** Cap for how close fit-to-bounds is allowed to zoom in on tight loops. */
private const val FOCUSED_MAX_ZOOM = 14.0

/** Bottom-side padding for fit-to-bounds so the route isn't covered by the bottom sheet. */
private const val FOCUSED_PADDING_PX = 96

@Composable
fun TracksMapView(
    tracks: List<Track>,
    focusedIds: Set<String>,
    shops: List<Shop>,
    shopsEnabled: Boolean,
    onClusterClick: (TrackCluster) -> Unit,
    onPolylineClick: (Track) -> Unit,
    onShopClick: (Shop) -> Unit,
    onMapClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            // No +/- buttons — pinch-to-zoom is universally understood and the
            // buttons just clutter the map.
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(DEFAULT_CENTER)
            isVerticalMapRepetitionEnabled = false
            isHorizontalMapRepetitionEnabled = false
        }
    }

    DisposableEffect(tracks, focusedIds, shops, shopsEnabled) {
        mapView.overlays.clear()

        // Bottom-most overlay: catches taps that no polyline / marker handled,
        // so tapping empty map area clears the current focus.
        val mapEvents = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (focusedIds.isNotEmpty()) {
                    onMapClear()
                    return true
                }
                return false
            }
            override fun longPressHelper(p: GeoPoint?): Boolean = false
        })
        mapView.overlays.add(mapEvents)

        val isFocusing = focusedIds.isNotEmpty()

        for (track in tracks) {
            if (track.route.isEmpty()) continue
            val points = track.route.map { GeoPoint(it.lat, it.lng) }
            val isFocused = track.uuid in focusedIds
            val color = colorForDifficulty(track.difficulty)

            // Wide invisible click target — gives fingers a fair chance to hit a thin polyline.
            val hitTarget = Polyline().apply {
                setPoints(points)
                outlinePaint.color = color
                outlinePaint.strokeWidth = 22f
                outlinePaint.alpha = 0
                setOnClickListener { _, _, _ ->
                    onPolylineClick(track)
                    true
                }
            }
            mapView.overlays.add(hitTarget)

            // Visible polyline. Dim the unfocused ones when focusing so the eye lands on the right routes.
            val poly = Polyline().apply {
                setPoints(points)
                outlinePaint.color = color
                outlinePaint.strokeWidth = if (isFocused) 9f else 6f
                outlinePaint.alpha = when {
                    !isFocusing -> 200
                    isFocused -> 255
                    else -> 70
                }
            }
            mapView.overlays.add(poly)
        }

        val clusters = groupTracksByStartPoint(tracks)
        for (cluster in clusters) {
            val isClusterFocused = cluster.tracks.any { it.uuid in focusedIds }
            val marker = Marker(mapView).apply {
                position = GeoPoint(cluster.position.lat, cluster.position.lng)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = buildClusterMarker(
                    context = context,
                    difficulty = cluster.primaryDifficulty,
                    count = cluster.tracks.size,
                    isSelected = isClusterFocused,
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

        if (shopsEnabled) {
            for (shop in shops) {
                for (loc in shop.locations) {
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(loc.lat, loc.lng)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        icon = buildShopMarker(context)
                        title = shop.name
                        setOnMarkerClickListener { _, _ ->
                            onShopClick(shop)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            }
        }

        mapView.invalidate()
        onDispose { /* MapView reused via remember */ }
    }

    // Camera moves only when the user actively focuses tracks. On deselect
    // (and on filter changes that empty/shrink the visible set) we leave the
    // camera where it is, so tapping nearby elements doesn't fling the user
    // back to the country overview. Initial Serbia view comes from the
    // mapView factory above.
    DisposableEffect(focusedIds, tracks) {
        if (focusedIds.isNotEmpty()) {
            val focusedTracks = tracks.filter { it.uuid in focusedIds }
            val points = focusedTracks.flatMap { t -> t.route.map { GeoPoint(it.lat, it.lng) } }
            if (points.isNotEmpty()) {
                mapView.post {
                    val bounds = BoundingBox.fromGeoPointsSafe(points)
                    mapView.zoomToBoundingBox(bounds, true, FOCUSED_PADDING_PX, FOCUSED_MAX_ZOOM, 650L)
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
    Difficulty.easy -> DifficultyMapColors.Easy.toArgb()
    Difficulty.medium -> DifficultyMapColors.Medium.toArgb()
    Difficulty.hard -> DifficultyMapColors.Hard.toArgb()
}
