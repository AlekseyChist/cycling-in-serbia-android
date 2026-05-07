package com.cyclinginserbia.app.data.repository

import com.cyclinginserbia.app.data.local.db.track.TrackDao
import com.cyclinginserbia.app.data.local.db.track.toEntity
import com.cyclinginserbia.app.data.local.db.track.toModel
import com.cyclinginserbia.app.data.local.preferences.SyncPreferences
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.data.supabase.TrackDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val trackDao: TrackDao,
    private val syncPreferences: SyncPreferences,
) {
    fun observePublishedTracks(): Flow<List<Track>> =
        trackDao.observePublished().map { rows -> rows.map { it.toModel() } }

    fun observeTrack(legacyId: String): Flow<Track?> =
        trackDao.observeByLegacyId(legacyId).map { it?.toModel() }

    suspend fun refreshIfStale() {
        if (isStale()) refresh()
    }

    suspend fun refresh() {
        val rows = supabase.from("tracks").select {
            filter { eq("is_published", true) }
            order(column = "sort_order", order = Order.ASCENDING)
        }.decodeList<TrackDto>()
        trackDao.upsertAll(rows.map { it.toEntity() })
        syncPreferences.setTracksLastSync(System.currentTimeMillis())
    }

    fun gpxPublicUrl(fileName: String): String =
        supabase.storage.from("gpx-files").publicUrl(fileName)

    private suspend fun isStale(): Boolean {
        if (trackDao.count() == 0) return true
        val lastSync = syncPreferences.tracksLastSyncAt.first()
        return System.currentTimeMillis() - lastSync > TTL_MILLIS
    }

    private companion object {
        const val TTL_MILLIS = 6L * 60L * 60L * 1000L
    }
}
