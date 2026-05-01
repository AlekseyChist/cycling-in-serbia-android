package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventStatus
import com.cyclinginserbia.app.data.model.EventType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

internal object EventGenerator {

    private data class RecurringTemplate(
        val name: String,
        val dayOfWeek: DayOfWeek,
        val time: LocalTime,
        val location: String,
        val type: EventType,
        val description: String,
        val idPrefix: String,
    )

    private val templates = listOf(
        RecurringTemplate(
            name = "DBB Dark-On-Draft",
            dayOfWeek = DayOfWeek.THURSDAY,
            time = LocalTime.of(18, 0),
            location = "Belgrade, Serbia",
            type = EventType.groupRide,
            description = "Evening workout ride. Check Strava club for the latest route and meeting point.",
            idPrefix = "strava-fallback-dod",
        ),
        RecurringTemplate(
            name = "DBB Coffee Ride",
            dayOfWeek = DayOfWeek.SATURDAY,
            time = LocalTime.of(9, 30),
            location = "Žorža Klemansoa 27V, Belgrade, Serbia",
            type = EventType.groupRide,
            description = "Social weekend coffee ride. Check Strava club for the latest route and details.",
            idPrefix = "strava-fallback-cr",
        ),
        RecurringTemplate(
            name = "DBB Rekafary Ride",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            time = LocalTime.of(8, 0),
            location = "Savsko pristanište, Belgrade, Serbia",
            type = EventType.groupRide,
            description = "Social morning ride. Check Strava club for the latest route and details.",
            idPrefix = "strava-fallback-rek",
        ),
        RecurringTemplate(
            name = "DBB Burekfast Club",
            dayOfWeek = DayOfWeek.TUESDAY,
            time = LocalTime.of(7, 0),
            location = "Gundulićev venac, Belgrade, Serbia",
            type = EventType.granfondo,
            description = "Workout morning ride. Check Strava club for the latest route and details.",
            idPrefix = "strava-fallback-bf",
        ),
    )

    fun generate(today: LocalDate = LocalDate.now(), weeksAhead: Int = 4): List<Event> {
        return templates
            .flatMap { tpl ->
                val firstOccurrence = nextOccurrenceOnOrAfter(today, tpl.dayOfWeek)
                (0 until weeksAhead).map { weekOffset ->
                    val date = firstOccurrence.plusWeeks(weekOffset.toLong())
                    Event(
                        id = "${tpl.idPrefix}-$weekOffset",
                        name = tpl.name,
                        date = date,
                        time = tpl.time,
                        location = tpl.location,
                        type = tpl.type,
                        status = EventStatus.upcoming,
                        description = tpl.description,
                        organizer = "DBB Club",
                        isFromStrava = true,
                    )
                }
            }
            .filter { !it.date.isBefore(today) }
            .sortedWith(compareBy({ it.date }, { it.time }))
    }

    private fun nextOccurrenceOnOrAfter(from: LocalDate, target: DayOfWeek): LocalDate {
        val diff = ((target.value - from.dayOfWeek.value) + 7) % 7
        return from.plusDays(diff.toLong())
    }
}
