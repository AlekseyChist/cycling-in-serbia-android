package com.cyclinginserbia.app.data.local.db.track

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks WHERE is_published = 1 ORDER BY sort_order ASC")
    fun observePublished(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE legacy_id = :legacyId LIMIT 1")
    fun observeByLegacyId(legacyId: String): Flow<TrackEntity?>

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(items: List<TrackEntity>)

    @Query("DELETE FROM tracks")
    suspend fun clear()
}
