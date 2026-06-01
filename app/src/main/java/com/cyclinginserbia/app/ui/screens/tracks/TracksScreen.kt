package com.cyclinginserbia.app.ui.screens.tracks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Shop
import com.cyclinginserbia.app.data.model.Surface as SurfaceType
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.components.SearchField
import com.cyclinginserbia.app.ui.theme.AppColors
import com.cyclinginserbia.app.ui.theme.ChipColors
import com.cyclinginserbia.app.ui.theme.ChipPalette
import kotlinx.coroutines.launch

private val CollapsedSheetPeekHeight = 96.dp

// When the user focuses one or more tracks, the sheet rises just enough to
// reveal the SheetHeader plus the first focused card — about 240 dp on most
// devices. The user can still pull it higher to browse the full list.
private val FocusedSheetPeekHeight = 240.dp

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

    // Sheet stays in PartiallyExpanded state on track tap — we just grow the
    // peek height so the user sees a hint of what they selected (header +
    // first focused card), without yanking the full list over the map. They
    // can pull the sheet up themselves to browse the rest.
    val peekHeight by animateDpAsState(
        targetValue = if (state.isFocused) FocusedSheetPeekHeight else CollapsedSheetPeekHeight,
        label = "tracks-peek-height",
    )

    fun clearFocusAndCollapse() {
        viewModel.clearFocus()
        scope.launch { sheetState.partialExpand() }
    }

    // Hardware/system back: when the user has focused on one or more tracks,
    // the natural action is "go back to seeing all tracks", not "leave the
    // screen". So back clears focus and collapses the sheet to its peek
    // state. When focus is empty, fall through to default nav back.
    BackHandler(enabled = state.isFocused, onBack = ::clearFocusAndCollapse)

    val showInitialLoading = state.isInitialLoading && state.tracks.isEmpty()
    val showError = state.tracks.isEmpty() && !state.isInitialLoading && state.syncError != null

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = AppColors.Background,
        sheetContent = {
            if (showInitialLoading || showError) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(
                            if (showError) R.string.tracks_error_load else R.string.loading,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.Gray600,
                    )
                }
            } else {
                SheetTracksList(
                    state = state,
                    listState = listState,
                    onTrackClick = onTrackClick,
                    onToggleFavorite = viewModel::onToggleFavorite,
                    onClearFilters = viewModel::clearFilters,
                    onClearFocus = viewModel::clearFocus,
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                showInitialLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                showError -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    ErrorView(
                        message = state.syncError?.message ?: stringResource(R.string.tracks_error_unknown),
                        onRetry = viewModel::sync,
                    )
                }

                else -> {
                    var selectedShop by remember { mutableStateOf<Shop?>(null) }

                    TracksMapView(
                        tracks = state.visible,
                        focusedIds = state.focusedIds,
                        shops = state.shops,
                        shopsEnabled = state.shopsEnabled,
                        onClusterClick = { cluster ->
                            viewModel.onFocusTracks(cluster.tracks.map { it.uuid }.toSet())
                        },
                        onPolylineClick = { track ->
                            viewModel.onFocusTracks(setOf(track.uuid))
                        },
                        onShopClick = { shop -> selectedShop = shop },
                        onMapClear = ::clearFocusAndCollapse,
                        modifier = Modifier.fillMaxSize(),
                    )

                    val regions by remember(state.tracks) {
                        derivedStateOf { state.tracks.map { it.region }.distinct().sorted() }
                    }
                    val activeFilterCount = activeFilterCount(state)
                    var showFilters by remember { mutableStateOf(false) }

                    FloatingHeader(
                        query = state.query,
                        activeFilterCount = activeFilterCount,
                        showSyncError = state.syncError != null && !state.isSyncing,
                        onQueryChange = viewModel::onQueryChange,
                        onOpenFilters = { showFilters = true },
                        onRetrySync = viewModel::sync,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(top = 76.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        MapLegend()
                        ShopsToggleButton(
                            enabled = state.shopsEnabled,
                            onClick = viewModel::onToggleShops,
                        )
                    }

                    if (selectedShop != null) {
                        ShopDetailsSheet(
                            shop = selectedShop!!,
                            onDismiss = { selectedShop = null },
                        )
                    }

                    if (showFilters) {
                        FiltersModalSheet(
                            difficulty = state.difficulty,
                            surface = state.surface,
                            rideType = state.rideType,
                            region = state.region,
                            regions = regions,
                            favoritesOnly = state.favoritesOnly,
                            onDifficultyChange = viewModel::onDifficultyChange,
                            onSurfaceChange = viewModel::onSurfaceChange,
                            onRideTypeChange = viewModel::onRideTypeChange,
                            onRegionChange = viewModel::onRegionChange,
                            onToggleFavoritesOnly = viewModel::onToggleFavoritesOnly,
                            onReset = viewModel::clearFilters,
                            onDismiss = { showFilters = false },
                        )
                    }
                }
            }
        }
    }
}

private fun activeFilterCount(state: TracksUiState): Int {
    var n = 0
    if (state.difficulty != DifficultyFilter.ALL) n++
    if (state.surface != SurfaceFilter.ALL) n++
    if (state.rideType != RideTypeFilter.ALL) n++
    if (state.region != null) n++
    if (state.favoritesOnly) n++
    return n
}

@Composable
private fun FloatingHeader(
    query: String,
    activeFilterCount: Int,
    showSyncError: Boolean,
    onQueryChange: (String) -> Unit,
    onOpenFilters: () -> Unit,
    onRetrySync: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Background),
            ) {
                SearchField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.tracks_search_placeholder),
                )
            }
            FilterButton(
                activeCount = activeFilterCount,
                onClick = onOpenFilters,
            )
        }
        if (showSyncError) {
            SyncErrorBanner(onRetry = onRetrySync)
        }
    }
}

@Composable
private fun FilterButton(activeCount: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .shadow(elevation = 6.dp, shape = CircleShape),
        shape = CircleShape,
        color = AppColors.Background,
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center) {
            BadgedBox(
                badge = {
                    if (activeCount > 0) {
                        Badge(
                            containerColor = AppColors.Primary,
                            contentColor = AppColors.PrimaryForeground,
                        ) {
                            Text(
                                text = activeCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = stringResource(R.string.tracks_filters),
                    tint = AppColors.Gray700,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersModalSheet(
    difficulty: DifficultyFilter,
    surface: SurfaceFilter,
    rideType: RideTypeFilter,
    region: String?,
    regions: List<String>,
    favoritesOnly: Boolean,
    onDifficultyChange: (DifficultyFilter) -> Unit,
    onSurfaceChange: (SurfaceFilter) -> Unit,
    onRideTypeChange: (RideTypeFilter) -> Unit,
    onRegionChange: (String?) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.tracks_filters),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            FilterSection(title = stringResource(R.string.tracks_difficulty)) {
                FilterChipRow(
                    entries = DifficultyFilter.entries,
                    selected = difficulty,
                    label = { stringResource(it.labelRes) },
                    onSelect = onDifficultyChange,
                )
            }
            FilterSection(title = stringResource(R.string.tracks_surface)) {
                FilterChipRow(
                    entries = SurfaceFilter.entries,
                    selected = surface,
                    label = { stringResource(it.labelRes) },
                    onSelect = onSurfaceChange,
                )
            }
            FilterSection(title = stringResource(R.string.tracks_ride_type)) {
                FilterChipRow(
                    entries = RideTypeFilter.entries,
                    selected = rideType,
                    label = { stringResource(it.labelRes) },
                    onSelect = onRideTypeChange,
                )
            }
            if (regions.isNotEmpty()) {
                FilterSection(title = stringResource(R.string.tracks_region)) {
                    FilterChipRow(
                        entries = listOf<String?>(null) + regions,
                        selected = region,
                        label = { it ?: stringResource(R.string.tracks_all_regions) },
                        onSelect = onRegionChange,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.tracks_favorites_only),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = favoritesOnly,
                    onCheckedChange = { onToggleFavoritesOnly() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.PrimaryForeground,
                        checkedTrackColor = AppColors.Primary,
                        uncheckedThumbColor = AppColors.Background,
                        uncheckedTrackColor = AppColors.Gray300,
                        uncheckedBorderColor = AppColors.Gray300,
                    ),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.tracks_reset),
                        color = AppColors.Gray700,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.PrimaryForeground,
                    ),
                ) {
                    Text(text = stringResource(R.string.tracks_apply), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = AppColors.Gray600,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun SyncErrorBanner(onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.Amber50,
        border = BorderStroke(1.dp, AppColors.Amber200),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.tracks_cached_banner),
                style = TextStyle(
                    color = AppColors.Amber800,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            TextButton(
                onClick = onRetry,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    color = AppColors.Primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun <T> FilterChipRow(
    entries: List<T>,
    selected: T,
    label: @Composable (T) -> String,
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
        targetValue = if (isSelected) AppColors.Primary else AppColors.Gray100,
        animationSpec = tween(150),
        label = "pill-bg",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.Background else AppColors.Gray700,
        animationSpec = tween(150),
        label = "pill-text",
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.height(34.dp),
        shape = RoundedCornerShape(50),
        color = background,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp),
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
    state: TracksUiState,
    listState: LazyListState,
    onTrackClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
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
            totalCount = state.tracks.size,
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
                    text = stringResource(R.string.tracks_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.tracks_empty_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Gray600,
                    textAlign = TextAlign.Center,
                )
                if (state.hasActiveFilters) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onClearFilters) {
                        Text(
                            stringResource(R.string.tracks_clear_filters),
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
                        isFavorite = track.uuid in state.favoriteIds,
                        onClick = { onTrackClick(track.legacyId) },
                        onToggleFavorite = { onToggleFavorite(track.uuid) },
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
                isFocused && sheetCount == 1 -> stringResource(R.string.tracks_header_one_selected)
                isFocused -> stringResource(R.string.tracks_header_focused, sheetCount, totalCount)
                sheetCount == totalCount -> pluralStringResource(R.plurals.tracks_count, totalCount, totalCount)
                else -> stringResource(R.string.tracks_header_partial, sheetCount, totalCount)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (isFocused) {
            IconButton(
                onClick = onClearFocus,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.tracks_show_all),
                    tint = AppColors.Gray700,
                )
            }
        }
    }
}

@Composable
private fun TrackCard(
    track: Track,
    isSelected: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val borderColor = if (isSelected) AppColors.Primary else Color.Transparent
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            // AppColors.Card is the same hex as Background in light, but in dark
            // it's #1C1C1E vs the sheet's #0F0F0F — gives the cards a visible
            // edge against the sheet without relying on shadows that don't read
            // on dark surfaces.
            containerColor = if (isSelected) AppColors.OrangeTint else AppColors.Card,
        ),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Image(
                    painter = painterResource(trackThumbnailRes(track)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.Gray100),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f).padding(end = 36.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DifficultyChip(track.difficulty)
                        SurfaceChip(track.surface)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = track.region,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.Gray600,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Stat(stringResource(R.string.tracks_stat_distance), "${track.distanceKm} km")
                        Spacer(Modifier.width(20.dp))
                        Stat(stringResource(R.string.tracks_stat_elevation), "${track.elevationM} m")
                    }
                }
            }
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.tracks_fav_remove else R.string.tracks_fav_add,
                    ),
                    tint = if (isFavorite) AppColors.Primary else AppColors.Gray500,
                )
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
    val label = stringResource(
        when (difficulty) {
            Difficulty.easy -> R.string.difficulty_easy
            Difficulty.medium -> R.string.difficulty_medium
            Difficulty.hard -> R.string.difficulty_hard
        },
    )
    SoftChip(label = label, palette = palette)
}

@Composable
private fun SurfaceChip(surface: SurfaceType) {
    val palette = when (surface) {
        SurfaceType.road -> ChipColors.Road
        SurfaceType.gravel -> ChipColors.Gravel
        SurfaceType.mixed -> ChipColors.Mixed
    }
    val label = stringResource(
        when (surface) {
            SurfaceType.road -> R.string.surface_road
            SurfaceType.gravel -> R.string.surface_gravel
            SurfaceType.mixed -> R.string.surface_mixed
        },
    )
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
        Text(stringResource(R.string.tracks_error_load), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = AppColors.Gray600)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

@Composable
private fun ShopsToggleButton(enabled: Boolean, onClick: () -> Unit) {
    val container = if (enabled) AppColors.Primary else AppColors.Background
    val tint = if (enabled) AppColors.PrimaryForeground else AppColors.Gray700
    Surface(
        modifier = Modifier
            .size(40.dp)
            .shadow(elevation = 4.dp, shape = CircleShape),
        shape = CircleShape,
        color = container,
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Outlined.Storefront,
                contentDescription = stringResource(
                    if (enabled) R.string.tracks_shops_hide else R.string.tracks_shops_show,
                ),
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopDetailsSheet(shop: Shop, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = shop.category,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Gray500,
            )
            Text(
                text = shop.description,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Gray700,
            )
            shop.locations.firstOrNull()?.address?.let { addr ->
                Text(
                    text = addr,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.Gray600,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                shop.locations.firstOrNull()?.let { loc ->
                    Button(
                        onClick = { openInMaps(context, loc.lat, loc.lng, shop.name) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.tracks_open_in_maps))
                    }
                }
                TextButton(
                    onClick = { openLink(context, shop.link) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(shop.linkLabel)
                }
            }
        }
    }
}

private fun openInMaps(context: android.content.Context, lat: Double, lng: Double, label: String) {
    // Generic geo: URI + chooser — leaves transport mode out of it (per
    // feedback_navigation_no_mode.md: a user might drive with bike on rack,
    // walk, taxi, etc.). Falls back gracefully to whatever maps app is installed.
    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    val chooser = Intent.createChooser(intent, context.getString(R.string.tracks_maps_chooser))
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}

private fun openLink(context: android.content.Context, link: String) {
    val intent = if (link.startsWith("tel:")) Intent(Intent.ACTION_DIAL, Uri.parse(link))
    else Intent(Intent.ACTION_VIEW, Uri.parse(link))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
