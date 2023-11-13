package org.mozilla.tiktokreporter

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp
import org.mozilla.tiktokreporter.util.parcelable
import javax.inject.Inject

@AndroidEntryPoint
class ScreenRecorderService : Service() {

    @Inject
    lateinit var screenRecorderManager: ScreenRecorderManager

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val deleteIntent = PendingIntent.getService(
            applicationContext,
            1,
            Intent(applicationContext, ScreenRecorderService::class.java).apply {
                action = Actions.SHOW_NOTIFICATION.toString()
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopRecordingIntent = PendingIntent.getService(
            applicationContext,
            2,
            Intent(applicationContext, ScreenRecorderService::class.java).apply {
                action = Actions.STOP.toString()
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "screen_recording_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Screen recording...")
            .setContentText("TikTokRecorder started screen recording.")
            .setOngoing(true)
            .setDeleteIntent(deleteIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop recording",
                stopRecordingIntent
            )
            .build()

        when (intent.action) {
            Actions.START.toString() -> {
                val activityResult = intent.parcelable<ActivityResult>("activityResult")

                onSdkVersionAndUp(Build.VERSION_CODES.Q) {
                    startForeground(
                        1,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } ?: startForeground(1, notification)

                screenRecorderManager.startRecording(
                    activityResult?.resultCode ?: -1,
                    activityResult?.data ?: Intent()
                )
            }

            Actions.SHOW_NOTIFICATION.toString() -> {
                with(NotificationManagerCompat.from(applicationContext)) {
                    if (
                        ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        notify(1, notification)
                    }
                }
            }

            Actions.STOP.toString() -> {
                screenRecorderManager.stopRecording()
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    enum class Actions {
        START,
        SHOW_NOTIFICATION,
        STOP
    }
}
