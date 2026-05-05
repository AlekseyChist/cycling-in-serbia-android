package com.cyclinginserbia.app.ui.screens.tracks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface as SurfaceType
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.components.SearchField
import com.cyclinginserbia.app.ui.theme.AppColors
import com.cyclinginserbia.app.ui.theme.ChipColors
import com.cyclinginserbia.app.ui.theme.ChipPalette
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracksScreen(
    onTrackClick: (String) -> Unit,
    viewModel: TracksViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun focusAndReveal(ids: Set<String>) {
        viewModel.onFocusTracks(ids)
        scope.launch { sheetState.expand() }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 96.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = AppColors.Background,
        sheetContent = {
            when (val s = state) {
                is TracksUiState.Ready -> SheetTracksList(
                    state = s,
                    listState = listState,
                    onTrackClick = onTrackClick,
                    onClearFilters = viewModel::clearFilters,
                    onClearFocus = viewModel::clearFocus,
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Loading…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.Gray600,
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is TracksUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                is TracksUiState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { ErrorView(s.message, onRetry = viewModel::load) }

                is TracksUiState.Ready -> {
                    TracksMapView(
                        tracks = s.visible,
                        focusedIds = s.focusedIds,
                        onClusterClick = { cluster ->
                            focusAndReveal(cluster.tracks.map { it.uuid }.toSet())
                        },
                        onPolylineClick = { track ->
                            focusAndReveal(setOf(track.uuid))
                        },
                        onMapClear = viewModel::clearFocus,
                        modifier = Modifier.fillMaxSize(),
                    )

                    FloatingHeader(
                        query = s.query,
                        difficulty = s.difficulty,
                        surface = s.surface,
                        onQueryChange = viewModel::onQueryChange,
                        onDifficultyChange = viewModel::onDifficultyChange,
                        onSurfaceChange = viewModel::onSurfaceChange,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingHeader(
    query: String,
    difficulty: DifficultyFilter,
    surface: SurfaceFilter,
    onQueryChange: (String) -> Unit,
    onDifficultyChange: (DifficultyFilter) -> Unit,
    onSurfaceChange: (SurfaceFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.Background),
        ) {
            SearchField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "Search tracks",
            )
        }
        FilterChipRow(
            entries = DifficultyFilter.entries,
            selected = difficulty,
            label = { it.label },
            onSelect = onDifficultyChange,
        )
        FilterChipRow(
            entries = SurfaceFilter.entries,
            selected = surface,
            label = { it.label },
            onSelect = onSurfaceChange,
        )
    }
}

@Composable
private fun <T> FilterChipRow(
    entries: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(entries) { entry ->
            PillChip(
                text = label(entry),
                isSelected = entry == selected,
                onClick = { onSelect(entry) },
            )
        }
    }
}

@Composable
private fun PillChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        targetValue = if (isSelected) AppColors.Primary else AppColors.Background,
        animationSpec = tween(150),
        label = "pill-bg",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.Background else AppColors.Gray700,
        animationSpec = tween(150),
        label = "pill-text",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .height(34.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(50)),
        shape = RoundedCornerShape(50),
        color = background,
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SheetTracksList(
    state: TracksUiState.Ready,
    listState: LazyListState,
    onTrackClick: (String) -> Unit,
    onClearFilters: () -> Unit,
    onClearFocus: () -> Unit,
) {
    val sheetTracks = state.sheetTracks

    LaunchedEffect(state.focusedIds, sheetTracks) {
        if (state.focusedIds.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        SheetHeader(
            sheetCount = sheetTracks.size,
            totalCount = state.all.size,
            isFocused = state.isFocused,
            onClearFocus = onClearFocus,
        )

        if (sheetTracks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "No tracks match your filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Try a different difficulty, surface, or search term.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Gray600,
                    textAlign = TextAlign.Center,
                )
                if (state.hasActiveFilters) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onClearFilters) {
                        Text(
                            "Clear filters",
                            color = AppColors.Primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(sheetTracks, key = { it.uuid }) { track ->
                    TrackCard(
                        track = track,
                        isSelected = state.isFocused,
                        onClick = { onTrackClick(track.legacyId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetHeader(
    sheetCount: Int,
    totalCount: Int,
    isFocused: Boolean,
    onClearFocus: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = when {
                isFocused && sheetCount == 1 -> "1 selected route"
                isFocused -> "$sheetCount of $totalCount focused"
                sheetCount == totalCount -> "$totalCount tracks"
                else -> "$sheetCount of $totalCount tracks"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (isFocused) {
            TextButton(onClick = onClearFocus) {
                Text(
                    text = "Show all",
                    color = AppColors.Primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TrackCard(track: Track, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) AppColors.Primary else Color.Transparent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppColors.OrangeTint else AppColors.Background,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Transparent),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DifficultyChip(track.difficulty)
                SurfaceChip(track.surface)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = track.region,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Gray600,
            )
            Spacer(Modifier.height(10.dp))
            Row {
                Stat("Distance", "${track.distanceKm} km")
                Spacer(Modifier.width(24.dp))
                Stat("Elevation", "${track.elevationM} m")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isSelected) 2.dp else 0.dp)
                .background(borderColor),
        )
    }
}

@Composable
private fun DifficultyChip(difficulty: Difficulty) {
    val palette = when (difficulty) {
        Difficulty.easy -> ChipColors.Easy
        Difficulty.medium -> ChipColors.Medium
        Difficulty.hard -> ChipColors.Hard
    }
    val label = when (difficulty) {
        Difficulty.easy -> "Easy"
        Difficulty.medium -> "Medium"
        Difficulty.hard -> "Hard"
    }
    SoftChip(label = label, palette = palette)
}

@Composable
private fun SurfaceChip(surface: SurfaceType) {
    val palette = when (surface) {
        SurfaceType.road -> ChipColors.Road
        SurfaceType.gravel -> ChipColors.Gravel
        SurfaceType.mixed -> ChipColors.Mixed
    }
    val label = when (surface) {
        SurfaceType.road -> "Road"
        SurfaceType.gravel -> "Gravel"
        SurfaceType.mixed -> "Mixed"
    }
    SoftChip(label = label, palette = palette)
}

@Composable
private fun SoftChip(label: String, palette: ChipPalette) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = palette.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = AppColors.Gray600)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Couldn't load tracks", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = AppColors.Gray600)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
