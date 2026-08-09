package com.cyclinginserbia.app.data.repository

import android.util.Log
import com.cyclinginserbia.app.data.model.Event
import com.cyclinginserbia.app.data.strava.StravaService
import com.cyclinginserbia.app.data.strava.toEvents
import com.cyclinginserbia.app.data.supabase.EventDto
import com.cyclinginserbia.app.util.EventQuery
import java.time.LocalDate
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val stravaService: StravaService,
    private val supabase: SupabaseClient,
) {

    // Process-lifetime cache. First caller pays the network round-trip;
    // subsequent calls (Events tab, EventDetail, prefetch from Onboarding)
    // resolve instantly. Cleared only on process death.
    @Volatile private var cached: List<Event>? = null
    private val mutex = Mutex()

    suspend fun getEvents(): List<Event> {
        val all = cached ?: mutex.withLock {
            cached ?: fetch().also { cached = it }
        }
        // Hide events whose date has already passed. Re-evaluated on every call
        // so the list self-cleans across midnight; the cache keeps the full set.
        return EventQuery.upcoming(all, LocalDate.now())
    }

    suspend fun getEventById(id: String): Event? =
        getEvents().firstOrNull { it.id == id }

    // Two sources, merged: recurring DBB club rides come live from Strava,
    // special / one-off events are curated by hand in the Supabase `events`
    // table. Either source failing degrades gracefully to the other.
    private suspend fun fetch(): List<Event> {
        val strava = runCatching { stravaService.fetchClubEvents().toEvents() }
            .onFailure { Log.w(TAG, "Strava live fetch failed; falling back to EventGenerator", it) }
            .getOrElse { EventGenerator.generate() }

        val manual = runCatching { fetchSupabaseEvents() }
            .onFailure { Log.w(TAG, "Supabase events fetch failed; skipping manual events", it) }
            .getOrDefault(emptyList())

        return merge(strava, manual)
    }

    private suspend fun fetchSupabaseEvents(): List<Event> =
        supabase.from("events").select {
            filter { eq("is_published", true) }
            order(column = "event_date", order = Order.ASCENDING)
        }.decodeList<EventDto>().map { it.toEvent() }

    private fun merge(strava: List<Event>, manual: List<Event>): List<Event> {
        // Keyed by Strava id when present, else name+date. A manual row with a
        // matching key overrides the Strava one (curated takes precedence), so
        // an organizer can e.g. mark a recurring ride sold out from the table.
        val byKey = LinkedHashMap<String, Event>()
        strava.forEach { byKey[it.dedupeKey()] = it }
        manual.forEach { byKey[it.dedupeKey()] = it }
        return byKey.values.sortedWith(compareBy({ it.date }, { it.time }))
    }

    private fun Event.dedupeKey(): String =
        stravaEventId?.let { "strava:$it" } ?: "nd:${name.trim().lowercase()}|$date"

    private companion object {
        const val TAG = "EventRepository"
    }
}
