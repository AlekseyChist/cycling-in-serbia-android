package com.cyclinginserbia.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String) {
    data object Onboarding : Destination("onboarding")
    data object Tracks : Destination("tracks")
    data object Events : Destination("events")
    data object Shops : Destination("shops")
    data object Regulations : Destination("regulations")

    data object TrackDetail : Destination("tracks/{trackId}") {
        fun create(trackId: String) = "tracks/$trackId"
        const val ARG_TRACK_ID = "trackId"
    }

    data object EventDetail : Destination("events/{eventId}") {
        fun create(eventId: String) = "events/$eventId"
        const val ARG_EVENT_ID = "eventId"
    }
}

data class BottomTab(
    val destination: Destination,
    val label: String,
    val icon: ImageVector,
)

val bottomTabs = listOf(
    BottomTab(Destination.Tracks, "Tracks", Icons.Outlined.Map),
    BottomTab(Destination.Events, "Events", Icons.Outlined.Event),
    BottomTab(Destination.Shops, "Shops", Icons.Outlined.Storefront),
    BottomTab(Destination.Regulations, "Rules", Icons.Outlined.Description),
)
