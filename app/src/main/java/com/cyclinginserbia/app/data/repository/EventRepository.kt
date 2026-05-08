package com.cyclinginserbia.app.data.repository

import android.util.Log
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.strava.StravaService
import com.cyclinginserbia.app.data.strava.toEvents
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val stravaService: StravaService,
) {

    // Process-lifetime cache. First caller pays the network round-trip;
    // subsequent calls (Events tab, EventDetail, prefetch from Onboarding)
    // resolve instantly. Cleared only on process death.
    @Volatile private var cached: List<Event>? = null
    private val mutex = Mutex()

    suspend fun getEvents(): List<Event> {
        cached?.let { return it }
        return mutex.withLock {
            cached ?: fetch().also { cached = it }
        }
    }

    suspend fun getEventById(id: String): Event? =
        getEvents().firstOrNull { it.id == id }

    private suspend fun fetch(): List<Event> =
        runCatching { stravaService.fetchClubEvents().toEvents() }
            .onFailure { Log.w(TAG, "Strava live fetch failed; falling back to EventGenerator", it) }
            .getOrElse { EventGenerator.generate() }

    private companion object {
        const val TAG = "EventRepository"
    }
}
