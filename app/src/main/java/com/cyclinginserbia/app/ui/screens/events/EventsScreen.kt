package com.cyclinginserbia.app.ui.screens.events

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventType
import com.cyclinginserbia.app.ui.components.SearchField
import com.cyclinginserbia.app.ui.theme.AppColors
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        when (val s = state) {
            is EventsUiState.Loading -> CenterBox { CircularProgressIndicator() }
            is EventsUiState.Error -> CenterBox {
                ErrorView(message = s.message, onRetry = viewModel::load)
            }
            is EventsUiState.Ready -> {
                FilterHeader(
                    query = s.query,
                    category = s.category,
                    type = s.type,
                    onQueryChange = viewModel::onQueryChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onTypeChange = viewModel::onTypeChange,
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        s.visible.isEmpty() -> EmptyState(
                            state = s,
                            onClearFilters = viewModel::clearFilters,
                        )
                        else -> EventsList(events = s.visible, onEventClick = onEventClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun FilterHeader(
    query: String,
    category: EventCategoryFilter,
    type: EventTypeFilter,
    onQueryChange: (String) -> Unit,
    onCategoryChange: (EventCategoryFilter) -> Unit,
    onTypeChange: (EventTypeFilter) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Search events",
        )
        FilterChipRow(
            entries = EventCategoryFilter.entries,
            selected = category,
            label = { it.label },
            onSelect = onCategoryChange,
        )
        FilterChipRow(
            entries = EventTypeFilter.entries,
            selected = type,
            label = { it.label },
            onSelect = onTypeChange,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppColors.Gray200),
    )
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
        targetValue = if (isSelected) AppColors.Primary else AppColors.Gray100,
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
        modifier = Modifier.height(34.dp),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventsList(events: List<Event>, onEventClick: (String) -> Unit) {
    val grouped = events.groupBy(::monthYearKey)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        grouped.forEach { (monthYear, monthEvents) ->
            stickyHeader(key = "header-$monthYear") {
                MonthHeader(monthYear)
            }
            items(items = monthEvents, key = { it.id }) { event ->
                EventCard(event = event, onClick = { onEventClick(event.id) })
            }
        }
    }
}

@Composable
private fun MonthHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = AppColors.Gray500,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun EventCard(event: Event, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = AppColors.Card,
        border = BorderStroke(1.dp, AppColors.Gray200),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(120.dp)
                    .background(AppColors.Primary),
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = formatCardDate(event),
                    style = TextStyle(
                        color = AppColors.Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = event.name,
                    style = TextStyle(
                        color = AppColors.Gray900,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = event.location,
                    style = TextStyle(
                        color = AppColors.Gray500,
                        fontSize = 14.sp,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                TypeChip(event.type)
            }
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
            .background(AppColors.Gray100)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = AppColors.Gray700,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun EmptyState(
    state: EventsUiState.Ready,
    onClearFilters: () -> Unit,
) {
    val (title, subtitle) = when {
        state.category == EventCategoryFilter.COMMUNITY -> {
            "Community events coming soon" to
                "We're working on opening event submissions to local clubs and individual riders."
        }
        state.hasActiveFilters -> {
            "No events match your filters" to "Try a different category, type, or search term."
        }
        else -> {
            "No upcoming events" to "Check back later — new rides are scheduled every week."
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = TextStyle(
                color = AppColors.Gray900,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = TextStyle(
                color = AppColors.Gray500,
                fontSize = 14.sp,
            ),
            textAlign = TextAlign.Center,
        )
        if (state.hasActiveFilters && state.category != EventCategoryFilter.COMMUNITY) {
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onClearFilters) {
                Text("Clear filters", color = AppColors.Primary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Couldn't load events",
            style = TextStyle(
                color = AppColors.Gray900,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message,
            style = TextStyle(color = AppColors.Gray500, fontSize = 14.sp),
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
private val cardDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH)

private fun monthYearKey(event: Event): String {
    val month = event.date.month.getDisplayName(JavaTextStyle.FULL, Locale.ENGLISH)
    return "$month ${event.date.year}"
}

private fun formatCardDate(event: Event): String =
    "${event.date.format(cardDateFormatter)} · ${event.time.format(timeFormatter)}"
