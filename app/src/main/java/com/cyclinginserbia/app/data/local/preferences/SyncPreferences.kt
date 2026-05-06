package com.cyclinginserbia.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore by preferencesDataStore(name = "sync_prefs")

@Singleton
class SyncPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.syncDataStore

    val tracksLastSyncAt: Flow<Long> = store.data.map { it[TRACKS_LAST_SYNC] ?: 0L }

    val favoriteTrackIds: Flow<Set<String>> =
        store.data.map { it[FAVORITE_TRACK_IDS] ?: emptySet() }

    suspend fun setTracksLastSync(epochMillis: Long) {
        store.edit { it[TRACKS_LAST_SYNC] = epochMillis }
    }

    suspend fun toggleFavoriteTrack(uuid: String) {
        store.edit { prefs ->
            val current = prefs[FAVORITE_TRACK_IDS] ?: emptySet()
            prefs[FAVORITE_TRACK_IDS] = if (uuid in current) current - uuid else current + uuid
        }
    }

    private companion object {
        val TRACKS_LAST_SYNC = longPreferencesKey("tracks_last_sync_at")
        val FAVORITE_TRACK_IDS = stringSetPreferencesKey("favorite_track_ids")
    }
}
