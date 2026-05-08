package com.cyclinginserbia.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cyclinginserbia.app.ui.screens.events.EventDetailScreen
import com.cyclinginserbia.app.ui.screens.events.EventsScreen
import com.cyclinginserbia.app.ui.screens.onboarding.OnboardingScreen
import com.cyclinginserbia.app.ui.screens.regulations.RegulationsScreen
import com.cyclinginserbia.app.ui.screens.shops.ShopsScreen
import com.cyclinginserbia.app.ui.screens.trackdetail.TrackDetailScreen
import com.cyclinginserbia.app.ui.screens.tracks.TracksScreen

@Composable
fun RootNavigation(rootViewModel: RootViewModel) {
    val initialRoute by rootViewModel.initialRoute.collectAsStateWithLifecycle()
    // Splash screen is held by MainActivity until initialRoute resolves.
    val startRoute = initialRoute ?: return

    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute in bottomTabs.map { it.destination.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) AppBottomBar(nav, currentRoute)
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = startRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Onboarding.route) {
                OnboardingScreen(onFinished = {
                    rootViewModel.markOnboardingCompleted()
                    nav.navigate(Destination.Tracks.route) {
                        popUpTo(Destination.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Destination.Tracks.route) {
                TracksScreen(onTrackClick = { id ->
                    nav.navigate(Destination.TrackDetail.create(id))
                })
            }
            composable(Destination.Events.route) {
                EventsScreen(onEventClick = { id ->
                    nav.navigate(Destination.EventDetail.create(id))
                })
            }
            composable(Destination.Shops.route) { ShopsScreen() }
            composable(Destination.Regulations.route) { RegulationsScreen() }
            composable(Destination.TrackDetail.route) { entry ->
                val id = entry.arguments?.getString(Destination.TrackDetail.ARG_TRACK_ID).orEmpty()
                TrackDetailScreen(trackId = id, onBack = { nav.popBackStack() })
            }
            composable(Destination.EventDetail.route) { entry ->
                val id = entry.arguments?.getString(Destination.EventDetail.ARG_EVENT_ID).orEmpty()
                EventDetailScreen(eventId = id, onBack = { nav.popBackStack() })
            }
        }
    }
}

@Composable
private fun AppBottomBar(nav: NavHostController, currentRoute: String?) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.destination.route,
                onClick = {
                    nav.navigate(tab.destination.route) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
            )
        }
    }
}
