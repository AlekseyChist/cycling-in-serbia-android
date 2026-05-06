package com.cyclinginserbia.app.data.repository

import android.util.Log
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.strava.StravaService
import com.cyclinginserbia.app.data.strava.toEvents
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val stravaService: StravaService,
) {

    suspend fun getEvents(): List<Event> = runCatching { stravaService.fetchClubEvents().toEvents() }
        .onFailure { Log.w(TAG, "Strava live fetch failed; falling back to EventGenerator", it) }
        .getOrElse { EventGenerator.generate() }

    suspend fun getEventById(id: String): Event? =
        getEvents().firstOrNull { it.id == id }

    private companion object {
        const val TAG = "EventRepository"
    }
}
