package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.data.model.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor() {

    suspend fun getEvents(): List<Event> = EventGenerator.generate()

    suspend fun getEventById(id: String): Event? =
        getEvents().firstOrNull { it.id == id }
}
