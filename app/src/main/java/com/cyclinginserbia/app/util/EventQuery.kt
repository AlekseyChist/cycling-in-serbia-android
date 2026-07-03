package com.cyclinginserbia.app.util

import com.cyclinginserbia.app.data.model.Event
import java.time.LocalDate

/**
 * Hides past events from the input list.
 */
object EventQuery {
    fun upcoming(events: List<Event>, today: LocalDate): List<Event> =
        events.filter { it.date >= today }
}
