package com.cyclinginserbia.app.ui.screens.tracks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.theme.DifficultyEasy
import com.cyclinginserbia.app.ui.theme.DifficultyHard
import com.cyclinginserbia.app.ui.theme.DifficultyMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracksScreen(
    onTrackClick: (String) -> Unit,
    viewModel: TracksViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tracks") }) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when (val s = state) {
                is TracksUiState.Loading -> CircularProgressIndicator()
                is TracksUiState.Error -> ErrorView(s.message, onRetry = viewModel::load)
                is TracksUiState.Ready -> TracksList(tracks = s.tracks, onTrackClick = onTrackClick)
            }
        }
    }
}

@Composable
private fun TracksList(tracks: List<Track>, onTrackClick: (String) -> Unit) {
    if (tracks.isEmpty()) {
        Text("No tracks yet", style = MaterialTheme.typography.bodyLarge)
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tracks, key = { it.uuid }) { track ->
            TrackCard(track = track, onClick = { onTrackClick(track.legacyId) })
        }
    }
}

@Composable
private fun TrackCard(track: Track, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(difficultyColor(track.difficulty)),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = track.region,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Row {
                Stat("Distance", "${track.distanceKm} km")
                Spacer(Modifier.width(24.dp))
                Stat("Elevation", "${track.elevationM} m")
                Spacer(Modifier.width(24.dp))
                Stat("Surface", track.surface.name)
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Couldn't load tracks", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

internal fun difficultyColor(d: Difficulty): Color = when (d) {
    Difficulty.easy -> DifficultyEasy
    Difficulty.medium -> DifficultyMedium
    Difficulty.hard -> DifficultyHard
}
