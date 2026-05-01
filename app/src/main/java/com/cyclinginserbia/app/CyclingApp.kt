package com.cyclinginserbia.app

import android.app.Application
import android.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class CyclingApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().apply {
            load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
            userAgentValue = packageName
        }
    }
}
