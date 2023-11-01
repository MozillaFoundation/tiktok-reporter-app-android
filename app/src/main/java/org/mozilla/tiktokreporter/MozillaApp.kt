package org.mozilla.tiktokreporter

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "")

@HiltAndroidApp
class MozillaApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}