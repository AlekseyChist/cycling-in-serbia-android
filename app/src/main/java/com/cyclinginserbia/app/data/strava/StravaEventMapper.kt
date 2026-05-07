package com.cyclinginserbia.app.data.strava

import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventCategory
import com.cyclinginserbia.app.data.model.EventStatus
import com.cyclinginserbia.app.data.model.EventType
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

private val RECURRING_EVENT_TITLES = setOf(
    "DBB Dark-On-Draft",
    "DBB Coffee Ride",
    "DBB Rekafary Ride",
    "DBB Burekfast Club",
)

private val DEFAULT_ZONE = ZoneId.of("Europe/Belgrade")

fun List<StravaClubEventDto>.toEvents(now: ZonedDateTime = ZonedDateTime.now(DEFAULT_ZONE)): List<Event> =
    flatMap { it.toEvents(now) }
        .sortedWith(compareBy({ it.date }, { it.time }))

private fun StravaClubEventDto.toEvents(now: ZonedDateTime): List<Event> {
    val zone = zone?.let { runCatching { ZoneId.of(it) }.getOrNull() } ?: DEFAULT_ZONE
    val today = now.toLocalDate()

    val parsedOccurrences = upcomingOccurrences
        .mapNotNull { parseOccurrence(it, zone) }

    val upcoming = parsedOccurrences
        .filter { !it.toLocalDate().isBefore(today) }
        .sorted()

    if (upcoming.isNotEmpty()) {
        return upcoming.mapIndexed { idx, zdt -> toEvent(zdt, idx) }
    }

    if (title in RECURRING_EVENT_TITLES && parsedOccurrences.isNotEmpty()) {
        val nextOccurrence = nextRecurringOccurrence(parsedOccurrences.last(), now, zone)
        return listOf(toEvent(nextOccurrence, 0, idSuffix = "gen"))
    }

    return emptyList()
}

private fun parseOccurrence(raw: String, zone: ZoneId): ZonedDateTime? = try {
    OffsetDateTime.parse(raw).atZoneSameInstant(zone)
} catch (_: DateTimeParseException) {
    null
}

/**
 * Build the next recurring occurrence in the event's local time zone.
 * Uses local time-of-day from the last known occurrence so DST changes
 * since that occurrence are applied automatically (ZonedDateTime handles
 * the offset shift when the local time is reattached to the zone).
 */
private fun nextRecurringOccurrence(
    last: ZonedDateTime,
    now: ZonedDateTime,
    zone: ZoneId,
): ZonedDateTime {
    val localTime = last.toLocalTime()
    val targetWeekday = last.dayOfWeek
    val nowLocal = now.withZoneSameInstant(zone)
    val today = nowLocal.toLocalDate()

    val daysUntilTarget = ((targetWeekday.value - today.dayOfWeek.value + 7) % 7).toLong()
    var candidate = today.plusDays(daysUntilTarget).atTime(localTime).atZone(zone)
    if (!candidate.isAfter(nowLocal)) candidate = candidate.plusWeeks(1)
    return candidate
}

private fun StravaClubEventDto.toEvent(
    when_: ZonedDateTime,
    index: Int,
    idSuffix: String? = null,
): Event {
    val organizerName = listOfNotNull(
        organizingAthlete?.firstname?.takeIf { it.isNotBlank() },
        organizingAthlete?.lastname?.takeIf { it.isNotBlank() },
    ).joinToString(" ").takeIf { it.isNotBlank() }

    val idCore = listOfNotNull("strava", id.toString(), idSuffix, index.toString()).joinToString("-")

    return Event(
        id = idCore,
        name = title,
        date = when_.toLocalDate(),
        time = when_.toLocalTime().withSecond(0).withNano(0),
        location = address?.takeIf { it.isNotBlank() } ?: "See Strava for details",
        type = mapSkillToType(skillLevels),
        status = EventStatus.upcoming,
        description = description,
        organizer = organizerName,
        isFromStrava = true,
        stravaEventId = id.toString(),
        category = EventCategory.DBB,
    )
}

private fun mapSkillToType(skillLevels: Int?): EventType = when (skillLevels) {
    2 -> EventType.race
    1 -> EventType.granfondo
    else -> EventType.groupRide
}
