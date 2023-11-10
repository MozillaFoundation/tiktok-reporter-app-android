package org.mozilla.tiktokreporter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MozillaApp: Application() {
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "screen_recording_channel",
            "Screen recording notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}