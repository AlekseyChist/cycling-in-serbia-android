package com.cyclinginserbia.app.ui.screens.events

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.cyclinginserbia.app.data.model.Event
import java.time.ZoneId

object EventActions {

    private const val DEFAULT_DURATION_MS = 2L * 60L * 60L * 1000L

    fun addToCalendar(context: Context, event: Event) {
        val begin = event.date.atTime(event.time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val end = begin + DEFAULT_DURATION_MS
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.name)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description.orEmpty())
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No calendar app available", Toast.LENGTH_SHORT).show()
        }
    }

    fun openStrava(context: Context, event: Event) {
        val url = if (event.stravaEventId != null) {
            "https://www.strava.com/clubs/dbb-/group_events/${event.stravaEventId}"
        } else {
            "https://www.strava.com/clubs/dbb-"
        }
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No browser available", Toast.LENGTH_SHORT).show()
        }
    }
}
