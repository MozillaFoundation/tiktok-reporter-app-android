package org.mozilla.tiktokreporter.editvideo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.dataStore
import org.mozilla.tiktokreporter.util.videosCollection
import java.io.File
import javax.inject.Inject

@OptIn(UnstableApi::class)
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class EditVideoScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context, // application context => no leaking
    val player: Player
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiAction = Channel<UiAction>()
    val uiAction = _uiAction.receiveAsFlow()

    private var transformerListener: Transformer.Listener
    private var transformer: Transformer

    private var tempEditedVideoFile: File? = null

    init {
        player.prepare()
        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE

        transformerListener = object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                onTransformerFinished()
                super.onCompleted(composition, exportResult)
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) {
                viewModelScope.launch {
                    _uiAction.send(UiAction.ShowError(exportException.message.orEmpty()))
                }
                super.onError(composition, exportResult, exportException)
            }
        }
        transformer = Transformer.Builder(context)
            .addListener(transformerListener)
            .build()

        viewModelScope.launch {
            context.dataStore.data.map {
                it[Common.DATASTORE_KEY_VIDEO_URI]
            }
                .filterNotNull()
                .collect { videoUriString ->
                    val videoUri = videoUriString.toUri()

                    val mediaItem = MediaItem.fromUri(videoUri)
                    player.setMediaItem(mediaItem)
                    _state.update { state ->
                        state.copy(
                            mediaItem = mediaItem,
                            videoUri = videoUri
                        )
                    }
                }
        }
    }

    fun onPlayerStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                viewModelScope.launch {
                    if (!this@EditVideoScreenViewModel.state.value.seekBarRangeSet) {
                        _state.update { state ->
                            state.copy(
                                seekBarRangeSet = true,
                                videoDurationMs = player.duration,
                                seekBarRangeSelection = 0L..player.duration
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSeekBarRangeChange(range: ClosedRange<Long>) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    seekBarRangeSelection = range
                )
            }
        }
    }

    fun onSeekBarRangeChangeFinished() {
        viewModelScope.launch {
            if (state.value.seekBarRangeSelection.endInclusive - state.value.seekBarRangeSelection.start > 1000) {
                val editedMediaItem = MediaItem.Builder()
                    .setUri(state.value.videoUri)
                    .setClippingConfiguration(
                        MediaItem.ClippingConfiguration.Builder()
                            .setStartPositionMs(state.value.seekBarRangeSelection.start)
                            .setEndPositionMs(state.value.seekBarRangeSelection.endInclusive)
                            .build()
                    )
                    .build()

                player.setMediaItem(editedMediaItem)

                _state.update { state ->
                    state.copy(
                        editedMediaItem = editedMediaItem,
                        edited = true
                    )
                }
            } else {
                Toast.makeText(context, "The recording is too short!", Toast.LENGTH_SHORT).show()
                player.setMediaItem(state.value.mediaItem)

                _state.update { state ->
                    state.copy(
                        editedMediaItem = state.mediaItem,
                        edited = true
                    )
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun onDoneEditing() {
        viewModelScope.launch {
            _isLoading.update { true }

            onSeekBarRangeChangeFinished()

            tempEditedVideoFile = File(
                context.filesDir,
                "edited_recording"
            )
            if (state.value.edited) {
                transformer.start(state.value.editedMediaItem, tempEditedVideoFile!!.path)
            } else {
                transformer.start(state.value.mediaItem, tempEditedVideoFile!!.path)
            }
        }
    }

    private fun onTransformerFinished() {
        viewModelScope.launch {
            val currentTimeMillis = System.currentTimeMillis()
            val contentValues = ContentValues(4).apply {
                put(MediaStore.Video.Media.TITLE, "TikTok Recording")
                put(MediaStore.Video.Media.DATE_ADDED, (currentTimeMillis / 1000).toInt())
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.DISPLAY_NAME, "TikTok Recording_$currentTimeMillis.mp4")
            }

            val editedVideoUri = context.contentResolver.insert(videosCollection, contentValues) ?: return@launch
            withContext(Dispatchers.IO) {
                context.contentResolver.openOutputStream(editedVideoUri, "w").use { outputStream ->
                    outputStream?.let {
                        tempEditedVideoFile?.inputStream()?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                context.dataStore.edit {
                    it[Common.DATASTORE_KEY_VIDEO_URI] = editedVideoUri.toString()
                }

                tempEditedVideoFile?.delete()
                tempEditedVideoFile = null
            }

            _isLoading.update { false }
            _uiAction.send(UiAction.NavigateBack)
        }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }

    data class State(
        val seekBarRangeSet: Boolean = false,
        val mediaItem: MediaItem = MediaItem.EMPTY,
        val editedMediaItem: MediaItem = mediaItem,
        val videoUri: Uri = Uri.EMPTY,
        val videoDurationMs: Long = 0L,
        val seekBarRangeSelection: ClosedRange<Long> = 0L..videoDurationMs,
        val edited: Boolean = false
    )

    sealed class UiAction {
        data object NavigateBack : UiAction()
        data class ShowError(
            val message: String
        ) : UiAction()
    }
}