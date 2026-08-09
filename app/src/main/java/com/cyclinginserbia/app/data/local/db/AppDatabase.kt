package com.cyclinginserbia.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyclinginserbia.app.data.local.db.track.TrackDao
import com.cyclinginserbia.app.data.local.db.track.TrackEntity

// v2: added the difficulty / surface indices. Any schema change alters Room's
// identity hash, so the version MUST be bumped even when the columns are
// untouched — fallbackToDestructiveMigration only runs on a version change, so
// leaving this at 1 crashed every existing install with "Room cannot verify
// the data integrity" instead of silently rebuilding the cache.
@Database(
    entities = [TrackEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}
