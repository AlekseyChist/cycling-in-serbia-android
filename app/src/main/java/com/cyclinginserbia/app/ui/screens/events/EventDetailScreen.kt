package com.cyclinginserbia.app.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventStatus
import com.cyclinginserbia.app.data.model.EventType
import com.cyclinginserbia.app.data.model.TimelineItem
import com.cyclinginserbia.app.ui.theme.AppColors
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        when (val s = state) {
            is EventDetailUiState.Loading ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is EventDetailUiState.Error ->
                Text(s.message, modifier = Modifier.align(Alignment.Center))
            is EventDetailUiState.Ready -> Detail(event = s.event, onBack = onBack)
        }
    }
}

@Composable
private fun Detail(event: Event, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            HeroBanner(event)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                TitleSection(event)
                KeyInfoCard(event)
                event.organizer?.takeIf { event.isFromStrava }?.let { OrganizerCard(it) }
                DescriptionSection(event)
                if (event.toBring.isNotEmpty()) WhatToBringSection(event.toBring)
                if (event.timeline.isNotEmpty()) TimelineSection(event.timeline)
                ActionButtons(event)
                StatusFooter(event.status)
                Spacer(Modifier.height(8.dp))
            }
        }

        FloatingTopBar(
            onBack = onBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun FloatingTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        CircleIconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.events_back),
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
private fun HeroBanner(event: Event) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    listOf(AppColors.Primary, AppColors.PrimaryDark),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (event.isFromStrava) R.string.event_banner_dbb else R.string.event_banner_community,
                ),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun TitleSection(event: Event) {
    Column {
        Text(
            text = event.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.Foreground,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TypeChip(event.type)
            if (event.status != EventStatus.upcoming) StatusChip(event.status)
        }
    }
}

@Composable
private fun KeyInfoCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Muted),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            InfoRow(
                icon = Icons.Outlined.CalendarMonth,
                label = stringResource(R.string.event_info_datetime),
                primary = formatLongDate(event),
                secondary = event.time.format(timeFormatter),
            )
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                label = stringResource(R.string.event_info_location),
                primary = event.location,
            )
            if (event.distanceOptions.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Outlined.Person,
                    label = stringResource(R.string.event_info_distance),
                    primary = event.distanceOptions.joinToString(", "),
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    primary: String,
    secondary: String? = null,
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.Gray500,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.Gray500,
            )
            Text(
                text = primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.Foreground,
            )
            if (secondary != null) {
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Gray500,
                )
            }
        }
    }
}

@Composable
private fun OrganizerCard(organizer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.StravaChipBg),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.event_organized_by),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.Gray500,
                )
                Text(
                    text = organizer,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Foreground,
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(event: Event) {
    val text = event.description?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.event_description_fallback, event.location)
    Column {
        Text(
            text = stringResource(R.string.event_about),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Foreground,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.Gray500,
        )
    }
}

@Composable
private fun WhatToBringSection(items: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Cream100),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.event_what_to_bring),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Foreground,
            )
            items.forEach { line ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Emerald600,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.Foreground,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineSection(items: List<TimelineItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Muted),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.event_timeline),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Foreground,
                )
            }
            items.forEachIndexed { index, item ->
                TimelineRow(item = item, isLast = index == items.lastIndex)
            }
        }
    }
}

@Composable
private fun TimelineRow(item: TimelineItem, isLast: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary),
            )
            if (!isLast) {
                Spacer(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(AppColors.Primary.copy(alpha = 0.25f)),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(top = 0.dp)) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Foreground,
            )
        }
    }
}

@Composable
private fun ActionButtons(event: Event) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { EventActions.addToCalendar(context, event) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = AppColors.PrimaryForeground,
            ),
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.event_add_to_calendar), fontWeight = FontWeight.SemiBold)
        }
        if (event.isFromStrava) {
            OutlinedButton(
                onClick = { EventActions.openStrava(context, event) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, SolidColor(AppColors.Primary)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Primary),
            ) {
                Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.event_open_in_strava), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TypeChip(type: EventType) {
    val label = stringResource(
        when (type) {
            EventType.race -> R.string.eventtype_race
            EventType.granfondo -> R.string.eventtype_granfondo
            EventType.groupRide -> R.string.eventtype_group_ride
        },
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(AppColors.Gray100)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = AppColors.Gray700,
        )
    }
}

@Composable
private fun StatusChip(status: EventStatus) {
    val (labelRes, container, content) = when (status) {
        EventStatus.soldOut -> Triple(R.string.eventstatus_soldout, AppColors.Amber100, AppColors.Amber700)
        EventStatus.canceled -> Triple(R.string.eventstatus_canceled, AppColors.Red500, AppColors.Background)
        EventStatus.upcoming -> return
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(text = stringResource(labelRes), style = MaterialTheme.typography.labelMedium, color = content)
    }
}

@Composable
private fun StatusFooter(status: EventStatus) {
    if (status != EventStatus.upcoming) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Amber50),
    ) {
        Text(
            text = stringResource(R.string.event_status_recurring),
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.Amber700,
            modifier = Modifier.padding(12.dp),
        )
    }
}

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
private val longDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

private fun formatLongDate(event: Event): String = event.date.format(longDateFormatter)
