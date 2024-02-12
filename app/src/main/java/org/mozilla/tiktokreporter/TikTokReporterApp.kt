package org.mozilla.tiktokreporter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import mozilla.telemetry.glean.Glean
import org.mozilla.tiktokreporter.GleanMetrics.GleanBuildInfo
import org.mozilla.tiktokreporter.GleanMetrics.Pings

@HiltAndroidApp
class TikTokReporterApp: Application() {
    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "screen_recording_channel",
                "Screen recording notification",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "uploading_file_channel",
                "Uploading file notification",
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        Glean.setDebugViewTag("tiktokreport-and")
        Glean.setLogPings(true)
        Glean.registerPings(Pings)
        Glean.initialize(
            applicationContext = applicationContext,
            uploadEnabled = true,
            buildInfo = GleanBuildInfo.buildInfo
        )
    }
}