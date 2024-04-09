package org.mozilla.tiktokreporter

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.dataStore
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp
import org.mozilla.tiktokreporter.util.parcelable
import javax.inject.Inject

@AndroidEntryPoint
class ScreenRecorderService : Service() {

    enum class Actions {
        START, SHOW_NOTIFICATION, STOP
    }

    @Inject
    lateinit var screenRecorderManager: ScreenRecorderManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var notification: Notification
    private lateinit var timer: Job
    private var videoLength: Int = 0
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notification = createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Actions.START.toString() -> {
                val activityResult = intent.parcelable<ActivityResult>("activityResult")
                if (activityResult != null) {

                    onSdkVersionAndUp(Build.VERSION_CODES.Q) {
                        startForeground(
                            1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                        )
                    } ?: startForeground(1, notification)

                    videoLength = applicationContext.resources.getInteger(R.integer.video_length)
                    startTimer()

                    scope.launch {
                        try {
                            screenRecorderManager.startRecording(
                                code = activityResult.resultCode, data = activityResult.data ?: Intent()
                            )
                            this@ScreenRecorderService.dataStore.edit {
                                it[Common.DATASTORE_KEY_IS_RECORDING] = true
                            }
                        } catch (e: Exception) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    applicationContext,
                                    "Device incompatible for screen recording. Please send the TikTok link!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            this@ScreenRecorderService.dataStore.edit {
                                it[Common.DATASTORE_KEY_REDIRECT_FIRST_TAB] = true
                                it[Common.DATASTORE_KEY_IS_RECORDING] = false
                            }
                            this@ScreenRecorderService.stopSelf()
                        }

                    }
                }
            }

            Actions.SHOW_NOTIFICATION.toString() -> {
                with(NotificationManagerCompat.from(applicationContext)) {
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        notify(1, notification)
                    }
                }
            }

            Actions.STOP.toString() -> {
                cancelTimer()
                scope.launch {
                    screenRecorderManager.stopRecording()

                    this@ScreenRecorderService.dataStore.edit {
                        it[Common.DATASTORE_KEY_IS_RECORDING] = false
                    }

                    this@ScreenRecorderService.stopSelf()
                }
            }
        }

        return START_STICKY
    }

    private fun getDeleteIntent() = PendingIntent.getService(
        applicationContext, 1, Intent(applicationContext, ScreenRecorderService::class.java).apply {
            action = Actions.SHOW_NOTIFICATION.toString()
        }, PendingIntent.FLAG_IMMUTABLE
    )

    private fun getStopRecordingIntent() = PendingIntent.getService(
        applicationContext, 2, Intent(applicationContext, ScreenRecorderService::class.java).apply {
            action = Actions.STOP.toString()
        }, PendingIntent.FLAG_IMMUTABLE
    )

    private fun createNotification() =
        NotificationCompat.Builder(this, "screen_recording_channel").setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Screen recording...").setContentText("TikTokRecorder started screen recording.").setOngoing(true)
            .setDeleteIntent(getDeleteIntent()).addAction(
                R.drawable.ic_launcher_foreground, "Stop recording", getStopRecordingIntent()
            ).build()

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }


    private fun startCoroutineTimer() = scope.launch(Dispatchers.IO) {
        delay(videoLength.toLong())
        scope.launch {
            screenRecorderManager.stopRecording()

            this@ScreenRecorderService.dataStore.edit {
                it[Common.DATASTORE_KEY_IS_RECORDING] = false
            }

            this@ScreenRecorderService.stopSelf()
        }
    }

    private fun startTimer() {
        timer = startCoroutineTimer()
        timer.start()
    }

    private fun cancelTimer() {
        timer.cancel()
    }
}
