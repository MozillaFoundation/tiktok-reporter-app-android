package org.mozilla.tiktokreporter

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp
import javax.inject.Inject

@AndroidEntryPoint
class UploadRecordingService : Service() {

    @Inject
    lateinit var tikTokReporterRepository: TikTokReporterRepository

    enum class Actions {
        START,
        SHOW_NOTIFICATION,
        STOP
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var notification: Notification

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notification = createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Actions.START.toString() -> {
                val recordingUri = intent.getStringExtra("recordingUri")

                recordingUri?.let {
                    onSdkVersionAndUp(Build.VERSION_CODES.Q) {
                        startForeground(
                            2,
                            notification,
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                        )
                    } ?: startForeground(2, notification)

                    scope.launch {
                        tikTokReporterRepository.uploadRecording(recordingUri.toUri())
                    }
                }

            }

            Actions.SHOW_NOTIFICATION.toString() -> {
                with(NotificationManagerCompat.from(applicationContext)) {
                    if (
                        ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        notify(2, notification)
                    }
                }
            }

            Actions.STOP.toString() -> {
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun getDeleteIntent() = PendingIntent.getService(
        applicationContext,
        1,
        Intent(applicationContext, UploadRecordingService::class.java).apply {
            action = Actions.SHOW_NOTIFICATION.toString()
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun createNotification() = NotificationCompat.Builder(this, "uploading_file_channel")
        .setSmallIcon(R.drawable.fyp_reporter_logo)
        .setContentTitle("Uploading recording...")
        .setOngoing(true)
        .setDeleteIntent(getDeleteIntent())
        .build()
}