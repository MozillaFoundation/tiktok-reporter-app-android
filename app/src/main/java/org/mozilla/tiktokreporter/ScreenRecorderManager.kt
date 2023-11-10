package org.mozilla.tiktokreporter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
            setVideoFrameRate(10) // default 30
            setVideoEncodingBitRate(1080 * 10000)
        }

        val currentTimeMillis = System.currentTimeMillis()
        val contentValues = ContentValues(4).apply {
            put(MediaStore.Video.Media.TITLE, "TikTok Recording")
            put(MediaStore.Video.Media.DATE_ADDED, (currentTimeMillis / 1000).toInt())
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DISPLAY_NAME, "$currentTimeMillis.mp4")
        }

        val videosCollection = onSdkVersionAndUp(Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoUri = context.contentResolver.insert(videosCollection, contentValues)

        videoUri ?: throw IOException("Couldn't create media store entry")

        context.contentResolver.openFileDescriptor(videoUri, "w").use {
            mediaRecorder?.apply {
                setOutputFile(it?.fileDescriptor)
                prepare()
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

    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaProjection?.stop()
        virtualDisplay?.release()

        mediaRecorder = null
        mediaProjection = null
        virtualDisplay = null
    }
}