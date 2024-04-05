package org.mozilla.tiktokreporter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.dataStore
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp
import org.mozilla.tiktokreporter.util.videosCollection
import javax.inject.Inject
import kotlin.math.min


class ScreenRecorderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var mediaProjectionManager: MediaProjectionManager = context.getSystemService(MediaProjectionManager::class.java)
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null

    private var videoUri: Uri? = null

    private val mediaProjectionCallback = object : MediaProjection.Callback() {}

    private fun setupMediaProjectionAndRecorder(
        code: Int, data: Intent, recordingInfo: RecordingInfo
    ) {
        val videoWidth: Int = min(context.resources.getInteger(R.integer.video_width), recordingInfo.width)
        val videoHeight: Int = min(context.resources.getInteger(R.integer.video_height), recordingInfo.height)
        val videoFrameRate: Int = context.resources.getInteger(R.integer.video_frame_rate)
        val videoBitRate: Int = context.resources.getInteger(R.integer.video_bit_rate)
        mediaProjection = mediaProjectionManager.getMediaProjection(code, data)

        @Suppress("DEPRECATION")
        mediaRecorder = onSdkVersionAndUp(Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } ?: MediaRecorder()
        mediaRecorder?.apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoSize(videoWidth, videoHeight)
            setVideoFrameRate(videoFrameRate)
            setVideoEncodingBitRate(videoBitRate)
            setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
        }

        val currentTimeMillis = System.currentTimeMillis()
        val contentValues = ContentValues(4).apply {
            put(MediaStore.Video.Media.TITLE, "TikTok Recording")
            put(MediaStore.Video.Media.DATE_ADDED, (currentTimeMillis / 1000).toInt())
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DISPLAY_NAME, "TikTok Recording_$currentTimeMillis.mp4")
        }

        videoUri = context.contentResolver.insert(videosCollection, contentValues) ?: return

        try {
            context.contentResolver.openFileDescriptor(videoUri!!, "w").use {
                it?.let {
                    mediaRecorder?.apply {
                        setOutputFile(it.fileDescriptor)
                        prepare()
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }

        mediaProjection!!.registerCallback(mediaProjectionCallback, Handler(Looper.getMainLooper()))
    }

    private fun setupVirtualDisplay(recordingInfo: RecordingInfo) {
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture", /* name */
            recordingInfo.width, /* width */
            recordingInfo.height, /* height */
            recordingInfo.density, /* density */
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, /* flags */
            mediaRecorder?.surface, /* surface */
            null, /* callback */
            null /* handler */
        )
    }

    fun startRecording(
        code: Int, data: Intent
    ) {
        val recordingInfo = getRecordingInfo()
        if (mediaProjection == null || mediaRecorder == null) try {
            setupMediaProjectionAndRecorder(code, data, recordingInfo)
        } catch (e: Exception) {
            throw e
        }

        setupVirtualDisplay(recordingInfo)
        mediaRecorder?.start()
    }

    suspend fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder?.release()

        mediaProjection?.stop()
        virtualDisplay?.release()

        mediaRecorder = null
        mediaProjection = null
        virtualDisplay = null

        context.dataStore.edit {
            it[Common.DATASTORE_KEY_VIDEO_URI] = videoUri.toString()
        }
        videoUri = null
    }

    private fun getRecordingInfo(): RecordingInfo {
        val displayMetrics = context.resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val displayDensity = displayMetrics.densityDpi

        val configuration = context.resources.configuration
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)  // TODO: check warning

        val cameraWidth = camcorderProfile?.videoFrameWidth ?: -1
        val cameraHeight = camcorderProfile?.videoFrameHeight ?: -1
        val cameraFrameRate = camcorderProfile?.videoFrameRate ?: 30

        return calculateRecordingInfo(
            displayWidth = displayWidth,
            displayHeight = displayHeight,
            displayDensity = displayDensity,
            isLandscape = isLandscape,
            cameraWidth = cameraWidth,
            cameraHeight = cameraHeight,
            cameraFrameRate = cameraFrameRate
        )
    }

    private fun calculateRecordingInfo(
        displayWidth: Int,
        displayHeight: Int,
        displayDensity: Int,
        isLandscape: Boolean,
        cameraWidth: Int,
        cameraHeight: Int,
        cameraFrameRate: Int,
        sizePercentage: Int = 100
    ): RecordingInfo {
        val actualDisplayWidth = displayWidth * sizePercentage / 100
        val actualDisplayHeight = displayHeight * sizePercentage / 100

        if (cameraWidth == -1 && cameraHeight == -1) {
            return RecordingInfo(
                width = actualDisplayWidth, height = actualDisplayHeight, frameRate = cameraFrameRate, density = displayDensity
            )
        }

        var frameWidth = if (isLandscape) cameraWidth else cameraHeight
        var frameHeight = if (isLandscape) cameraHeight else cameraWidth

        if (frameWidth >= actualDisplayWidth && frameHeight >= actualDisplayHeight) {
            // Frame can hold the entire display. Use exact values.
            return RecordingInfo(
                width = actualDisplayWidth, height = actualDisplayHeight, frameRate = cameraFrameRate, density = displayDensity
            )
        }

        if (isLandscape) {
            frameWidth = actualDisplayWidth * frameHeight / actualDisplayHeight
        } else {
            frameHeight = actualDisplayHeight * frameWidth / actualDisplayWidth
        }

        return RecordingInfo(
            width = frameWidth, height = frameHeight, frameRate = cameraFrameRate, density = displayDensity
        )
    }

    private data class RecordingInfo(
        val width: Int,
        val height: Int,
        val frameRate: Int,
        val density: Int,
    )
}