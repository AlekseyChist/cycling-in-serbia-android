package com.cyclinginserbia.app.data.supabase

import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventCategory
import com.cyclinginserbia.app.data.model.EventStatus
import com.cyclinginserbia.app.data.model.EventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

/**
 * A manually-curated event row from the Supabase `events` table. These are
 * special / one-off rides (races, gran fondos, community events) entered by
 * hand in the Supabase dashboard, as opposed to the recurring DBB rides that
 * come live from Strava. Merged with the Strava feed in EventRepository.
 */
@Serializable
data class EventDto(
    val id: String,
    val name: String,
    @SerialName("event_date") val eventDate: String,
    @SerialName("event_time") val eventTime: String = "09:00:00",
    val location: String,
    val type: String = "group_ride",
    val status: String = "upcoming",
    val category: String = "COMMUNITY",
    val description: String? = null,
    val organizer: String? = null,
    @SerialName("distance_options") val distanceOptions: List<String> = emptyList(),
    @SerialName("is_published") val isPublished: Boolean = false,
    @SerialName("sort_order") val sortOrder: Int = 0,
) {
    fun toEvent(): Event = Event(
        id = "supabase-$id",
        name = name,
        date = LocalDate.parse(eventDate),
        time = parseTime(eventTime),
        location = location,
        type = parseType(type),
        status = parseStatus(status),
        description = description?.takeIf { it.isNotBlank() },
        organizer = organizer?.takeIf { it.isNotBlank() },
        isFromStrava = false,
        distanceOptions = distanceOptions,
        category = parseCategory(category),
    )

    private companion object {
        fun parseTime(raw: String): LocalTime =
            runCatching { LocalTime.parse(raw) }.getOrDefault(LocalTime.of(9, 0))

        fun parseType(raw: String): EventType = when (raw.trim().lowercase()) {
            "race" -> EventType.race
            "granfondo", "gran_fondo", "gran fondo" -> EventType.granfondo
            else -> EventType.groupRide
        }

        fun parseStatus(raw: String): EventStatus = when (raw.trim().lowercase()) {
            "soldout", "sold_out", "sold out" -> EventStatus.soldOut
            "canceled", "cancelled" -> EventStatus.canceled
            else -> EventStatus.upcoming
        }

        fun parseCategory(raw: String): EventCategory = when (raw.trim().uppercase()) {
            "DBB" -> EventCategory.DBB
            else -> EventCategory.COMMUNITY
        }
    }
}
