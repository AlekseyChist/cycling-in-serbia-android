package com.cyclinginserbia.app.data.strava

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StravaClubEventDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    @SerialName("activity_type") val activityType: String? = null,
    @SerialName("skill_levels") val skillLevels: Int? = null,
    @SerialName("upcoming_occurrences") val upcomingOccurrences: List<String> = emptyList(),
    val zone: String? = null,
    val address: String? = null,
    @SerialName("organizing_athlete") val organizingAthlete: StravaAthleteDto? = null,
)

@Serializable
data class StravaAthleteDto(
    val firstname: String? = null,
    val lastname: String? = null,
)
