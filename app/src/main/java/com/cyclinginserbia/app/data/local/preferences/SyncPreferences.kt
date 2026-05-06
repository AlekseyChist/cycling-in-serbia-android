package com.cyclinginserbia.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
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

    suspend fun setTracksLastSync(epochMillis: Long) {
        store.edit { it[TRACKS_LAST_SYNC] = epochMillis }
    }

    private companion object {
        val TRACKS_LAST_SYNC = longPreferencesKey("tracks_last_sync_at")
    }
}
