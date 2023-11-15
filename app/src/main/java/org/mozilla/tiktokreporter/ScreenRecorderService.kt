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
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.dataStore
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp
import org.mozilla.tiktokreporter.util.parcelable
import javax.inject.Inject

@AndroidEntryPoint
class ScreenRecorderService : Service() {

    enum class Actions {
        START,
        SHOW_NOTIFICATION,
        STOP
    }

    @Inject
    lateinit var screenRecorderManager: ScreenRecorderManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()

        when (intent?.action) {
            Actions.START.toString() -> {
                val activityResult = intent.parcelable<ActivityResult>("activityResult")

                if (activityResult != null) {

                    onSdkVersionAndUp(Build.VERSION_CODES.Q) {
                        startForeground(
                            1,
                            notification,
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                        )
                    } ?: startForeground(1, notification)


                    scope.launch {
                        screenRecorderManager.startRecording(
                            code = activityResult.resultCode,
                            data = activityResult.data ?: Intent()
                        )
                        this@ScreenRecorderService.dataStore.edit {
                            it[Common.IS_RECORDING_PREFERENCE_KEY] = true
                        }
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
                        notify(1, notification)
                    }
                }
            }

            Actions.STOP.toString() -> {
                scope.launch {
                    screenRecorderManager.stopRecording()

                    this@ScreenRecorderService.dataStore.edit {
                        it[Common.IS_RECORDING_PREFERENCE_KEY] = false
                    }

                    this@ScreenRecorderService.stopSelf()
                }
            }
        }

        return START_STICKY
    }

    private fun getDeleteIntent() = PendingIntent.getService(
        applicationContext,
        1,
        Intent(applicationContext, ScreenRecorderService::class.java).apply {
            action = Actions.SHOW_NOTIFICATION.toString()
        },
        PendingIntent.FLAG_IMMUTABLE
    )
    private fun getStopRecordingIntent() = PendingIntent.getService(
        applicationContext,
        2,
        Intent(applicationContext, ScreenRecorderService::class.java).apply {
            action = Actions.STOP.toString()
        },
        PendingIntent.FLAG_IMMUTABLE
    )
    private fun createNotification() = NotificationCompat.Builder(this, "screen_recording_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Screen recording...")
        .setContentText("TikTokRecorder started screen recording.")
        .setOngoing(true)
        .setDeleteIntent(getDeleteIntent())
        .addAction(
            R.drawable.ic_launcher_foreground,
            "Stop recording",
            getStopRecordingIntent()
        )
        .build()

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
