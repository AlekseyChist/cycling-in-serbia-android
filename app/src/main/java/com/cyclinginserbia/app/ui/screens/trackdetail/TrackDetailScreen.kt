package com.cyclinginserbia.app.ui.screens.trackdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Image
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.data.model.Surface
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.components.TrackMap
import com.cyclinginserbia.app.ui.screens.tracks.trackHeroRes
import com.cyclinginserbia.app.ui.theme.AppColors
import com.cyclinginserbia.app.ui.theme.ChipColors
import com.cyclinginserbia.app.ui.theme.ChipPalette

@Composable
fun TrackDetailScreen(
    trackId: String,
    onBack: () -> Unit,
    viewModel: TrackDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        when (val s = state) {
            is TrackDetailUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
            is TrackDetailUiState.Error -> Text(
                text = s.message,
                modifier = Modifier.align(Alignment.Center),
            )
            is TrackDetailUiState.Ready -> Detail(
                track = s.track,
                gpxUrl = s.gpxUrl,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun Detail(track: Track, gpxUrl: String?, onBack: () -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroPhoto(track)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TitleSection(track)
                StatsGrid(track)
                track.description?.takeIf { it.isNotBlank() }?.let { AboutSection(it) }
                track.safetyNotes?.takeIf { it.isNotBlank() }?.let { SafetyCard(it) }
                RoutePreview(track)
                ActionButtons(
                    hasGpx = gpxUrl != null,
                    onDownload = { downloadGpx(context, track, gpxUrl) },
                    onNavigate = { navigateToStart(context, track) },
                )
                DbbTipCard()
                Spacer(Modifier.height(8.dp))
            }
        }

        FloatingTopBar(
            onBack = onBack,
            onShare = { shareTrack(context, track, gpxUrl) },
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun HeroPhoto(track: Track) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(AppColors.Gray100),
    ) {
        Image(
            painter = painterResource(trackHeroRes(track)),
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun FloatingTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CircleIconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AppColors.Foreground,
            )
        }
        CircleIconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share",
                tint = AppColors.Foreground,
            )
        }
    }
}

@Composable
private fun CircleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AppColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
            content()
        }
    }
}

@Composable
private fun TitleSection(track: Track) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = track.name,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Foreground,
            ),
        )
        Text(
            text = track.region,
            style = TextStyle(fontSize = 14.sp, color = AppColors.Gray500),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DifficultyChip(track.difficulty)
            SurfaceChip(track.surface)
        }
    }
}

@Composable
private fun DifficultyChip(difficulty: Difficulty) {
    val palette = when (difficulty) {
        Difficulty.easy -> ChipColors.Easy
        Difficulty.medium -> ChipColors.Medium
        Difficulty.hard -> ChipColors.Hard
    }
    FilledChip(
        text = difficulty.name.replaceFirstChar { it.titlecase() },
        palette = palette,
    )
}

@Composable
private fun FilledChip(text: String, palette: ChipPalette) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = palette.text,
            ),
        )
    }
}

@Composable
private fun SurfaceChip(surface: Surface) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, AppColors.Gray300, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = surface.name.replaceFirstChar { it.titlecase() },
            style = TextStyle(
                fontSize = 12.sp,
                color = AppColors.Gray500,
            ),
        )
    }
}

@Composable
private fun StatsGrid(track: Track) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard("Distance", "${track.distanceKm} km", Modifier.weight(1f))
        StatCard("Elevation", "${track.elevationM} m", Modifier.weight(1f))
        StatCard("Time", track.estimatedTime?.takeIf { it.isNotBlank() } ?: "—", Modifier.weight(1f))
        StatCard(
            "Difficulty",
            track.difficulty.name.replaceFirstChar { it.titlecase() },
            Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.Muted)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 11.sp, color = AppColors.Gray500),
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Foreground,
            ),
        )
    }
}

@Composable
private fun AboutSection(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "About this route",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Foreground,
            ),
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                color = AppColors.Gray500,
                lineHeight = 22.sp,
            ),
        )
    }
}

@Composable
private fun SafetyCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Amber50)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.WarningAmber,
            contentDescription = null,
            tint = AppColors.Amber500,
            modifier = Modifier.size(20.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Safety Notes",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Amber700,
                ),
            )
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = AppColors.Amber700,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

@Composable
private fun RoutePreview(track: Track) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Route Preview",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Foreground,
            ),
        )
        TrackMap(
            track = track,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
    }
}

@Composable
private fun ActionButtons(
    hasGpx: Boolean,
    onDownload: () -> Unit,
    onNavigate: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onDownload,
            enabled = hasGpx,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = AppColors.PrimaryForeground,
                disabledContainerColor = AppColors.Gray200,
                disabledContentColor = AppColors.Gray500,
            ),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.FileDownload,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (hasGpx) "Download GPX" else "GPX unavailable",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
            )
        }

        OutlinedButton(
            onClick = onNavigate,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AppColors.Secondary,
                contentColor = AppColors.Foreground,
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Gray200),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Navigation,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Navigate to Start",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
            )
        }
    }
}

@Composable
private fun DbbTipCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Cream50)
            .padding(14.dp),
    ) {
        Text(
            text = buildDbbTip(),
            style = TextStyle(
                fontSize = 13.sp,
                color = AppColors.Gray500,
                lineHeight = 20.sp,
            ),
        )
    }
}

private fun buildDbbTip(): AnnotatedString = buildAnnotatedString {
    withStyle(
        SpanStyle(
            color = AppColors.Primary,
            fontWeight = FontWeight.Bold,
        ),
    ) { append("DBB Tip: ") }
    append(
        "Plan your ride — check the weather forecast, top up your bottles, " +
            "and let someone know your route before heading out.",
    )
}
