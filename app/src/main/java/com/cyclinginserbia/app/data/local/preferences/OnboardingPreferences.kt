package com.cyclinginserbia.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_prefs")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.onboardingDataStore

    // null = onboarding never completed; otherwise the versionCode from the
    // build that last marked it complete. Re-show onboarding on version
    // bumps so users see "what's new" copy when we update it.
    val lastCompletedVersionCode: Flow<Int?> =
        store.data.map { it[LAST_COMPLETED_VERSION_CODE] }

    suspend fun markCompleted(versionCode: Int) {
        store.edit { it[LAST_COMPLETED_VERSION_CODE] = versionCode }
    }

    private companion object {
        val LAST_COMPLETED_VERSION_CODE = intPreferencesKey("last_completed_version_code")
    }
}
