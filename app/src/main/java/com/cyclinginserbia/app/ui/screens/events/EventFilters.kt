package com.cyclinginserbia.app.ui.screens.events

import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventCategory
import com.cyclinginserbia.app.data.model.EventType

enum class EventCategoryFilter(val label: String) {
    ALL("All"),
    DBB("DBB Club"),
    COMMUNITY("Community"),
}

enum class EventTypeFilter(val label: String) {
    ALL("All Types"),
    RACE("Race"),
    GRANFONDO("Gran Fondo"),
    GROUP_RIDE("Group Ride"),
}

internal fun List<Event>.applyFilters(
    query: String,
    category: EventCategoryFilter,
    type: EventTypeFilter,
): List<Event> {
    val q = query.trim()
    return asSequence()
        .filter { event ->
            when (category) {
                EventCategoryFilter.ALL -> true
                EventCategoryFilter.DBB -> event.category == EventCategory.DBB
                EventCategoryFilter.COMMUNITY -> event.category == EventCategory.COMMUNITY
            }
        }
        .filter { event ->
            when (type) {
                EventTypeFilter.ALL -> true
                EventTypeFilter.RACE -> event.type == EventType.race
                EventTypeFilter.GRANFONDO -> event.type == EventType.granfondo
                EventTypeFilter.GROUP_RIDE -> event.type == EventType.groupRide
            }
        }
        .filter { event ->
            q.isEmpty() ||
                event.name.contains(q, ignoreCase = true) ||
                event.location.contains(q, ignoreCase = true)
        }
        .toList()
}
