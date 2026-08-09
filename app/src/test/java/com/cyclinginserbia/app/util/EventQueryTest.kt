package com.cyclinginserbia.app.util

import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventType
import com.cyclinginserbia.app.data.model.EventStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class EventQueryTest {

    private fun event(name: String, date: LocalDate): Event {
        return Event(
            id = name,
            name = name,
            date = date,
            time = LocalTime.of(9, 0),
            location = "L",
            type = EventType.groupRide,
            status = EventStatus.upcoming,
            description = null,
            organizer = null,
            isFromStrava = false
        )
    }

    private val today = LocalDate.of(2026, 7, 3)

    @Test
    fun drops_events_before_today() {
        val input = listOf(
            event("past", today.minusDays(1)),
            event("future", today.plusDays(1))
        )
        val result = EventQuery.upcoming(input, today)
        assertEquals(listOf("future"), result.map { it.name })
    }

    @Test
    fun keeps_event_on_today() {
        val input = listOf(event("todayEvent", today))
        val result = EventQuery.upcoming(input, today)
        assertEquals(1, result.size)
        assertEquals("todayEvent", result[0].name)
    }

    @Test
    fun keeps_future_events() {
        val input = listOf(
            event("f1", today.plusDays(2)),
            event("f2", today.plusDays(10))
        )
        val result = EventQuery.upcoming(input, today)
        assertEquals(2, result.size)
    }

    @Test
    fun preserves_input_order() {
        val input = listOf(
            event("C", today.plusDays(3)),
            event("A", today.plusDays(1)),
            event("B", today.plusDays(2))
        )
        val result = EventQuery.upcoming(input, today)
        assertEquals(listOf("C", "A", "B"), result.map { it.name })
    }

    @Test
    fun mixed_list_keeps_only_upcoming_in_order() {
        val input = listOf(
            event("old", today.minusDays(5)),
            event("today", today),
            event("soon", today.plusDays(1)),
            event("older", today.minusDays(1))
        )
        val result = EventQuery.upcoming(input, today)
        assertEquals(listOf("today", "soon"), result.map { it.name })
    }

    @Test
    fun empty_list_returns_empty() {
        val result = EventQuery.upcoming(emptyList(), today)
        assertEquals(0, result.size)
    }
}
