package com.cyclinginserbia.app.ui.screens.events

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventStatus
import com.cyclinginserbia.app.data.model.EventType
import java.time.format.DateTimeFormatter
import java.util.Locale

private val StravaOrangeDark = Color(0xFFE34402)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event") },
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
                is EventDetailUiState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is EventDetailUiState.Error ->
                    Text(s.message, modifier = Modifier.align(Alignment.Center))
                is EventDetailUiState.Ready -> Detail(event = s.event)
            }
        }
    }
}

@Composable
private fun Detail(event: Event) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HeroBanner(event)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            TitleSection(event)
            KeyInfoCard(event)
            event.organizer?.takeIf { event.isFromStrava }?.let { OrganizerCard(it) }
            DescriptionSection(event)
            ActionButtons(event)
            StatusFooter(event.status)
        }
    }
}

@Composable
private fun HeroBanner(event: Event) {
    val (top, bottom) = if (event.isFromStrava) {
        StravaOrange to StravaOrangeDark
    } else {
        MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.tertiary
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Brush.verticalGradient(listOf(top, bottom))),
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
                text = if (event.isFromStrava) "DBB Club Event" else "Event",
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            InfoRow(
                icon = Icons.Outlined.CalendarMonth,
                label = "Date & Time",
                primary = formatLongDate(event),
                secondary = event.time.format(timeFormatter),
            )
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                label = "Location",
                primary = event.location,
            )
            if (event.distanceOptions.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Distance Options",
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
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            if (secondary != null) {
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        colors = CardDefaults.cardColors(containerColor = StravaOrange.copy(alpha = 0.08f)),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(StravaOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = StravaOrange,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Organized by",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = organizer,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(event: Event) {
    val text = event.description?.takeIf { it.isNotBlank() }
        ?: "Join fellow cyclists for an exciting ride through the beautiful landscapes of ${event.location}."
    Column {
        Text(
            text = "About this event",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ActionButtons(event: Event) {
    val context = LocalContext.current
    if (event.isFromStrava) {
        val url = if (event.stravaEventId != null)
            "https://www.strava.com/clubs/dbb-/group_events/${event.stravaEventId}"
        else
            "https://www.strava.com/clubs/dbb-"
        Button(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StravaOrange,
                contentColor = Color.White,
            ),
        ) {
            Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Open in Strava", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TypeChip(type: EventType) {
    val label = when (type) {
        EventType.race -> "Race"
        EventType.granfondo -> "Gran Fondo"
        EventType.groupRide -> "Group Ride"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun StatusChip(status: EventStatus) {
    val (label, container, content) = when (status) {
        EventStatus.soldOut -> Triple(
            "Sold Out",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )
        EventStatus.canceled -> Triple(
            "Canceled",
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError,
        )
        EventStatus.upcoming -> return
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = content)
    }
}

@Composable
private fun StatusFooter(status: EventStatus) {
    if (status != EventStatus.upcoming) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
        ),
    ) {
        Text(
            text = "Recurring DBB club ride. Confirm route and meeting point on the club's Strava page before joining.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(12.dp),
        )
    }
}

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
private val longDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

private fun formatLongDate(event: Event): String = event.date.format(longDateFormatter)
