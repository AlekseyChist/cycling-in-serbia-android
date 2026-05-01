package com.cyclinginserbia.app.ui.screens.events

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EventsScreen(onEventClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Events — coming soon", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun EventDetailScreen(eventId: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Event $eventId", style = MaterialTheme.typography.titleLarge)
    }
}
