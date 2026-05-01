package com.cyclinginserbia.app.ui.screens.trackdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.components.TrackMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailScreen(
    trackId: String,
    onBack: () -> Unit,
    viewModel: TrackDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is TrackDetailUiState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is TrackDetailUiState.Error ->
                    Text(s.message, modifier = Modifier.align(Alignment.Center))
                is TrackDetailUiState.Ready -> Detail(track = s.track)
            }
        }
    }
}

@Composable
private fun Detail(track: Track) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TrackMap(
            track = track,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(track.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                track.region,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Stat("Distance", "${track.distanceKm} km")
                Stat("Elevation", "${track.elevationM} m")
                Stat("Difficulty", track.difficulty.name)
                Stat("Surface", track.surface.name)
            }
            track.description?.let {
                Spacer(Modifier.height(16.dp))
                Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            track.safetyNotes?.let {
                Spacer(Modifier.height(16.dp))
                Text("Safety", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}
