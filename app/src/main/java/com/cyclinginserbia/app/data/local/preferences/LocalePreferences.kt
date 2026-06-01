package com.cyclinginserbia.app.data.local.preferences

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Single DataStore delegate for the persisted language. Defined once at file
// scope so both the Hilt-injected LocalePreferences (used from ViewModels) and
// the static LocaleManager (used from Activity.attachBaseContext, where Hilt
// injection isn't available yet) read/write the SAME DataStore instance.
private val Context.localeDataStore by preferencesDataStore(name = "locale_prefs")

private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")

/**
 * Languages the user can pick in-app. [tag] is the BCP-47 language tag we feed
 * to [Locale.forLanguageTag]; null means "follow the phone's system locale".
 *
 * Serbian uses Latin script (sr-Latn) — closer to the Latin track/shop names
 * the rest of the UI shows, per the product decision.
 */
enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    EN("en"),
    RU("ru"),
    SR("sr-Latn");

    companion object {
        fun fromName(name: String?): AppLanguage =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}

@Singleton
class LocalePreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.localeDataStore

    val appLanguage: Flow<AppLanguage> =
        store.data.map { AppLanguage.fromName(it[KEY_APP_LANGUAGE]) }

    suspend fun setAppLanguage(language: AppLanguage) {
        store.edit { it[KEY_APP_LANGUAGE] = language.name }
    }
}

/**
 * Locale plumbing for the Activity layer. minSdk is 26 and the app is
 * Compose-only (no AppCompat), so we can't use AppCompatDelegate's per-app
 * locales. Instead we wrap the base Context with a Configuration carrying the
 * chosen locale in [MainActivity.attachBaseContext]; switching language then
 * just persists the choice and calls recreate().
 */
object LocaleManager {

    /**
     * Read the persisted language synchronously. Safe to call from
     * attachBaseContext (before Hilt injection): it's a tiny one-shot DataStore
     * read, and the splash screen is already held until RootViewModel resolves.
     */
    fun currentLanguageBlocking(context: Context): AppLanguage =
        AppLanguage.fromName(
            runBlocking { context.localeDataStore.data.first()[KEY_APP_LANGUAGE] },
        )

    /** Return a Context whose resources resolve against [language]'s locale. */
    fun wrap(base: Context, language: AppLanguage): Context {
        val locale = language.tag
            ?.let(Locale::forLanguageTag)
            ?: Resources.getSystem().configuration.locales[0]
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }
}
