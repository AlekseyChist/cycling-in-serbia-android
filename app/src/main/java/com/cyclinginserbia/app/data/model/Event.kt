package com.cyclinginserbia.app.data.model

import java.time.LocalDate
import java.time.LocalTime

enum class EventType { race, granfondo, groupRide }
enum class EventStatus { upcoming, soldOut, canceled }

data class TimelineItem(val time: String, val label: String)

data class Event(
    val id: String,
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val location: String,
    val type: EventType,
    val status: EventStatus,
    val description: String?,
    val organizer: String?,
    val isFromStrava: Boolean,
    val stravaEventId: String? = null,
    val distanceOptions: List<String> = emptyList(),
    val toBring: List<String> = emptyList(),
    val timeline: List<TimelineItem> = emptyList(),
)
