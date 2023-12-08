package org.mozilla.tiktokreporter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

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

//        TopLevel.identifier.set(UUID.randomUUID())
//        TopLevel.name.set("name")
//        TopLevel.fields.set("stringified json of the form")
//        Pings.tiktokReport.submit()
//
//        Glean.registerPings(Pings)
//        Glean.initialize(
//            applicationContext = applicationContext,
//            uploadEnabled = true,
//            buildInfo = GleanBuildInfo.buildInfo
//        )
    }
}