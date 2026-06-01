package com.cyclinginserbia.app.ui.screens.events

import androidx.annotation.StringRes
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.model.EventCategory
import com.cyclinginserbia.app.data.model.EventType

enum class EventCategoryFilter(@StringRes val labelRes: Int) {
    ALL(R.string.eventcat_all),
    DBB(R.string.eventcat_dbb),
    COMMUNITY(R.string.eventcat_community),
}

enum class EventTypeFilter(@StringRes val labelRes: Int) {
    ALL(R.string.eventtype_all),
    RACE(R.string.eventtype_race),
    GRANFONDO(R.string.eventtype_granfondo),
    GROUP_RIDE(R.string.eventtype_group_ride),
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
