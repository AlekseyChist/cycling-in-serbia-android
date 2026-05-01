package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.supabase.TrackDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {
    suspend fun getPublishedTracks(region: String? = null): List<Track> {
        val rows = supabase.from("tracks").select {
            filter {
                eq("is_published", true)
                if (region != null) eq("region", region)
            }
            order(column = "sort_order", order = io.github.jan.supabase.postgrest.query.Order.ASCENDING)
        }.decodeList<TrackDto>()
        return rows.map { it.toModel() }
    }

    suspend fun getTrackByLegacyId(legacyId: String): Track? {
        val row = supabase.from("tracks").select {
            filter { eq("legacy_id", legacyId) }
            limit(1)
        }.decodeSingleOrNull<TrackDto>()
        return row?.toModel()
    }

    fun gpxPublicUrl(fileName: String): String =
        supabase.storage.from("gpx-files").publicUrl(fileName)
}
