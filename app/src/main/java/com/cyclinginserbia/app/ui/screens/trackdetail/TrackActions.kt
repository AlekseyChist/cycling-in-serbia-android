package com.cyclinginserbia.app.ui.screens.trackdetail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.cyclinginserbia.app.data.model.Track

/**
 * Helpers that turn TrackDetail action buttons into real device actions.
 * Kept out of the composable file to keep UI focused on layout.
 */

internal fun downloadGpx(context: Context, track: Track, url: String?) {
    if (url == null || track.gpxFileName == null) {
        Toast.makeText(context, "GPX not available for this track", Toast.LENGTH_SHORT).show()
        return
    }

    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("${track.name}.gpx")
        .setDescription("Cycling in Serbia — ${track.region}")
        .setMimeType("application/gpx+xml")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, track.gpxFileName)

    runCatching {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(context, "Downloading ${track.name}.gpx", Toast.LENGTH_SHORT).show()
    }.onFailure {
        Toast.makeText(context, "Download failed: ${it.message}", Toast.LENGTH_LONG).show()
    }
}

internal fun navigateToStart(context: Context, track: Track) {
    val target = track.startPoint ?: track.coordinates ?: track.route.firstOrNull()
    if (target == null) {
        Toast.makeText(context, "No start coordinates", Toast.LENGTH_SHORT).show()
        return
    }
    val lat = target.lat
    val lng = target.lng
    val label = Uri.encode(track.name)

    // Hand the coordinates to whatever map / nav app the user prefers.
    // No transport mode — a person heading to the start might be driving with
    // a bike on the rack, walking, or taking a taxi; let them pick the tool
    // and the mode. Google Maps in Serbia doesn't do cycling routing anyway.
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)"),
    )
    val chooser = Intent.createChooser(intent, "Open in").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(chooser) }
        .onFailure { Toast.makeText(context, "No maps app installed", Toast.LENGTH_LONG).show() }
}

internal fun shareTrack(context: Context, track: Track, gpxUrl: String?) {
    val text = buildString {
        append(track.name)
        append(" — ")
        append(track.region)
        append(" (${track.distanceKm} km, ${track.elevationM} m)")
        gpxUrl?.let { append("\n").append(it) }
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, track.name)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share track").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}
