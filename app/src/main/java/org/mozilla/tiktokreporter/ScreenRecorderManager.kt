package org.mozilla.tiktokreporter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
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

class ScreenRecorderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val displayMetrics = context.resources.displayMetrics
    private val aspectRation =
        displayMetrics.heightPixels.toDouble() / displayMetrics.widthPixels.toDouble()

    private var mediaProjectionManager: MediaProjectionManager =
        context.getSystemService(MediaProjectionManager::class.java)
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null

    private var videoUri: Uri? = null

    private val mediaProjectionCallback = object : MediaProjection.Callback() {}

    private fun setupMediaProjectionAndRecorder(
        code: Int,
        data: Intent
    ) {
        mediaProjection = mediaProjectionManager.getMediaProjection(code, data)

        mediaRecorder = onSdkVersionAndUp(Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } ?: MediaRecorder()
        mediaRecorder?.apply {

            setVideoSource(MediaRecorder.VideoSource.SURFACE)

            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoSize(
                720,
                720.toDouble().times(aspectRation).toInt()
            )
        }

        val currentTimeMillis = System.currentTimeMillis()
        val contentValues = ContentValues(4).apply {
            put(MediaStore.Video.Media.TITLE, "TikTok Recording")
            put(MediaStore.Video.Media.DATE_ADDED, (currentTimeMillis / 1000).toInt())
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DISPLAY_NAME, "TikTok Recording_$currentTimeMillis.mp4")
        }

        videoUri = context.contentResolver.insert(videosCollection, contentValues) ?: return

        context.contentResolver.openFileDescriptor(videoUri!!, "w").use {
            it?.let {
                mediaRecorder?.apply {
                    setOutputFile(it.fileDescriptor)
                    prepare()
                }
            }
        }

        mediaProjection!!.registerCallback(mediaProjectionCallback, Handler(Looper.getMainLooper()))
    }

    private fun setupVirtualDisplay() {
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture", /* name */
            displayMetrics.widthPixels, /* width */
            displayMetrics.heightPixels, /* height */
            displayMetrics.densityDpi, /* density */
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, /* flags */
            mediaRecorder?.surface, /* surface */
            null, /* callback */
            null /* handler */
        )
    }

    fun startRecording(
        code: Int,
        data: Intent
    ) {
        if (mediaProjection == null || mediaRecorder == null)
            setupMediaProjectionAndRecorder(code, data)

        setupVirtualDisplay()
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
            it[Common.VIDEO_URI_PREFERENCE_KEY] = videoUri.toString()
        }
        videoUri = null
    }
}