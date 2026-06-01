package com.cyclinginserbia.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.cyclinginserbia.app.R

sealed class Destination(val route: String) {
    data object Onboarding : Destination("onboarding")
    data object Tracks : Destination("tracks")
    data object Events : Destination("events")
    data object Shops : Destination("shops")
    data object Regulations : Destination("regulations")
    data object Profile : Destination("profile")

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
    @StringRes val labelRes: Int,
    val icon: ImageVector,
)

val bottomTabs = listOf(
    BottomTab(Destination.Tracks, R.string.nav_tracks, Icons.Outlined.Map),
    BottomTab(Destination.Events, R.string.nav_events, Icons.Outlined.Event),
    BottomTab(Destination.Shops, R.string.nav_shops, Icons.Outlined.Storefront),
    BottomTab(Destination.Regulations, R.string.nav_regulations, Icons.Outlined.Description),
    BottomTab(Destination.Profile, R.string.nav_profile, Icons.Outlined.Person),
)
