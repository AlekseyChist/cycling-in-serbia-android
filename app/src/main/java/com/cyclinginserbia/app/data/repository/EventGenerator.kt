package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventStatus
import com.cyclinginserbia.app.data.model.EventType
import com.cyclinginserbia.app.data.model.TimelineItem
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
        val toBring: List<String>,
        val timeline: List<TimelineItem>,
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
            toBring = listOf(
                "Road or gravel bike in working order",
                "Helmet (mandatory)",
                "Front and rear lights — it gets dark fast",
                "Reflective layer or bright jersey",
                "Bidon and a snack for the effort",
            ),
            timeline = listOf(
                TimelineItem("18:00", "Meet at the start point"),
                TimelineItem("18:15", "Roll out together"),
                TimelineItem("19:30", "Mid-ride regroup"),
                TimelineItem("20:30", "Finish — optional drink afterwards"),
            ),
        ),
        RecurringTemplate(
            name = "DBB Coffee Ride",
            dayOfWeek = DayOfWeek.SATURDAY,
            time = LocalTime.of(9, 30),
            location = "Žorža Klemansoa 27V, Belgrade, Serbia",
            type = EventType.groupRide,
            description = "Social weekend coffee ride. Check Strava club for the latest route and details.",
            idPrefix = "strava-fallback-cr",
            toBring = listOf(
                "Any bike you can ride in a relaxed pace",
                "Helmet (mandatory)",
                "Bidon with water",
                "Cash or card for coffee stop",
                "Layers — Belgrade mornings can be cool",
            ),
            timeline = listOf(
                TimelineItem("09:30", "Meet at the cafe start"),
                TimelineItem("09:45", "Roll out — social pace"),
                TimelineItem("11:00", "Coffee and cake stop"),
                TimelineItem("12:30", "Back at the start"),
            ),
        ),
        RecurringTemplate(
            name = "DBB Rekafary Ride",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            time = LocalTime.of(8, 0),
            location = "Savsko pristanište, Belgrade, Serbia",
            type = EventType.groupRide,
            description = "Social morning ride. Check Strava club for the latest route and details.",
            idPrefix = "strava-fallback-rek",
            toBring = listOf(
                "Road or gravel bike",
                "Helmet (mandatory)",
                "Two bidons — it gets warm by the river",
                "Energy bar or banana",
                "Sunglasses and sunscreen",
            ),
            timeline = listOf(
                TimelineItem("08:00", "Meet at Savsko pristanište"),
                TimelineItem("08:15", "Roll out along the river"),
                TimelineItem("09:30", "Quick regroup and snack"),
                TimelineItem("11:00", "Finish — coffee at the start"),
            ),
        ),
        RecurringTemplate(
            name = "DBB Burekfast Club",
            dayOfWeek = DayOfWeek.TUESDAY,
            time = LocalTime.of(7, 0),
            location = "Gundulićev venac, Belgrade, Serbia",
            type = EventType.granfondo,
            description = "Workout morning ride. Check Strava club for the latest route and details.",
            idPrefix = "strava-fallback-bf",
            toBring = listOf(
                "Road bike in good shape",
                "Helmet (mandatory)",
                "Bidon and gel or bar for the effort",
                "Cash for burek and coffee",
                "Light jacket — early starts are cool",
            ),
            timeline = listOf(
                TimelineItem("07:00", "Meet at Gundulićev venac"),
                TimelineItem("07:10", "Roll out — workout pace"),
                TimelineItem("08:30", "Burek and coffee stop"),
                TimelineItem("09:30", "Back home or onward to work"),
            ),
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
                        toBring = tpl.toBring,
                        timeline = tpl.timeline,
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
