package org.mozilla.tiktokreporter.editvideo

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFramePercent
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.components.MozillaProgressIndicator
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.util.CollectWithLifecycle
import org.mozilla.tiktokreporter.util.scale

private const val SEEK_BAR_FRAMES_COUNT = 10

@Composable
fun EditVideoScreen(
    viewModel: EditVideoScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    DialogContainer { dialogState ->

        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val playerListener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    viewModel.onPlayerStateChanged(playbackState)
                }
            }
            viewModel.player.addListener(playerListener)

            onDispose {
                viewModel.player.removeListener(playerListener)
            }
        }

        CollectWithLifecycle(
            flow = viewModel.uiAction,
            onCollect = { action ->
                when (action) {
                    EditVideoScreenViewModel.UiAction.NavigateBack -> onNavigateBack()
                    is EditVideoScreenViewModel.UiAction.ShowError -> {

                    }
                }
            }
        )

        EditVideoScreenContent(
            state = state,
            isLoading = isLoading,
            player = viewModel.player,
            onNavigateBack = onNavigateBack,
            onDoneEditing = viewModel::onDoneEditing,
            onSeekBarRangeChange = viewModel::onSeekBarRangeChange,
            onSeekBarRangeChangeFinished = viewModel::onSeekBarRangeChangeFinished,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EditVideoScreenContent(
    state: EditVideoScreenViewModel.State,
    isLoading: Boolean,
    player: Player,
    onNavigateBack: () -> Unit,
    onDoneEditing: () -> Unit,
    onSeekBarRangeChange: (ClosedRange<Long>) -> Unit,
    onSeekBarRangeChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {

    var seekbarAspectRatio by remember { mutableFloatStateOf(0f) }

    MozillaScaffold(
        modifier = modifier,
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navItem = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = MozillaColor.TextColor
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MozillaDimension.M, vertical = MozillaDimension.L),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.L)
        ) {

            if (state.videoUri != Uri.EMPTY && state.mediaItem != MediaItem.EMPTY) {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    player = player,
                    onAspectRatioChanged = { ratio ->
                        seekbarAspectRatio = ratio * SEEK_BAR_FRAMES_COUNT
                    }
                )

                if (seekbarAspectRatio > 0) {
                    SeekBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(seekbarAspectRatio)
                            .background(Color.Black),
                        uri = state.videoUri,
                        valueRange = 0L..state.videoDurationMs,
                        seekBarRangeSelection = state.seekBarRangeSelection,
                        onSeekBarRangeChange = onSeekBarRangeChange,
                        onSeekBarRangeChangeFinished = onSeekBarRangeChangeFinished
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MozillaProgressIndicator(
                        modifier = Modifier.size(40.dp)
                            .align(Alignment.Center)
                    )
                }
            } else {
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.done_editing),
                    onClick = onDoneEditing
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(
    modifier: Modifier = Modifier,
    player: Player,
    onAspectRatioChanged: (ratio: Float) -> Unit
) {
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PlayerView(context).also {
                it.player = player

                it.setShowNextButton(false)
                it.setShowPreviousButton(false)
                it.setShowFastForwardButton(false)
                it.setShowRewindButton(false)
                it.setShowVrButton(false)
                it.setShowShuffleButton(false)
                it.setShowSubtitleButton(false)
                it.setShowPlayButtonIfPlaybackIsSuppressed(false)

                it.setAspectRatioListener { targetAspectRatio, _, _ ->
                    onAspectRatioChanged(targetAspectRatio)
                }
            }
        },
        update = {
            when (lifecycle) {
                Lifecycle.Event.ON_PAUSE -> {
                    it.onPause()
                    it.player?.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    it.onResume()
                }

                else -> Unit
            }
        }
    )
}

@Composable
private fun SeekBar(
    modifier: Modifier = Modifier,
    uri: Uri,
    valueRange: ClosedRange<Long>,
    seekBarRangeSelection: ClosedRange<Long>,
    thumbWidth: Dp = MozillaDimension.XS,
    onSeekBarRangeChange: (ClosedRange<Long>) -> Unit,
    onSeekBarRangeChangeFinished: () -> Unit
) {
    val context = LocalContext.current


    // scales userValue from within valueRange.start..valueRange.end to within minPx..maxPx
    fun scaleToOffset(minPx: Float, maxPx: Float, userValue: Float) =
        scale(
            min = valueRange.start.toFloat(),
            max = valueRange.endInclusive.toFloat(),
            num = userValue,
            targetMin = minPx,
            targetMax = maxPx
        )

    Box(
        modifier = modifier
    ) {
        RangeSlider(
            modifier = Modifier.fillMaxSize(),
            value = seekBarRangeSelection.start.toFloat()..seekBarRangeSelection.endInclusive.toFloat(),
            valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat(),
            onValueChange = {
                onSeekBarRangeChange(it.start.toLong()..it.endInclusive.toLong())
            },
            onValueChangeFinished = onSeekBarRangeChangeFinished,
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                activeTickColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
                disabledThumbColor = Color.Transparent,
                disabledActiveTrackColor = Color.Transparent,
                disabledActiveTickColor = Color.Transparent,
                disabledInactiveTrackColor = Color.Transparent,
                disabledInactiveTickColor = Color.Transparent,
            )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    val startThumbOffset = scaleToOffset(
                        minPx = 0f,
                        maxPx = this.size.width,
                        userValue = seekBarRangeSelection.start.toFloat()
                    ) - thumbWidth.div(2).value
                    val endThumbOffset = scaleToOffset(
                        minPx = 0f,
                        maxPx = this.size.width,
                        userValue = seekBarRangeSelection.endInclusive.toFloat()
                    ) - thumbWidth.div(2).value

                    drawSeekBar(
                        startThumbOffset = startThumbOffset,
                        endThumbOffset = endThumbOffset,
                        thumbWidth = thumbWidth
                    )
                }
        ) {
            for (frame in 0 until SEEK_BAR_FRAMES_COUNT) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .videoFramePercent(frame.toDouble().div(SEEK_BAR_FRAMES_COUNT))
                        .build(),
                    imageLoader = ImageLoader.Builder(context)
                        .components {
                            add(VideoFrameDecoder.Factory())
                        }
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f),
                )
            }
        }
    }
}

private fun ContentDrawScope.drawSeekBar(
    startThumbOffset: Float,
    endThumbOffset: Float,
    thumbWidth: Dp,
) {
    drawContent()

    drawLine(
        color = Color(0xC4E2E2E2),
        start = Offset(
            x = 0f,
            y = size.center.y
        ),
        end = Offset(
            x = startThumbOffset + thumbWidth.value.div(2),
            y = size.center.y
        ),
        strokeWidth = size.height,
    )

    drawRect(
        color = MozillaColor.Red,
        topLeft = Offset(
            x = startThumbOffset,
            y = 0f
        ),
        size = Size(thumbWidth.value, size.height)
    )

    drawLine(
        color = Color(0xC4E2E2E2),
        start = Offset(
            x = endThumbOffset + thumbWidth.value.div(2),
            y = size.center.y
        ),
        end = Offset(
            x = size.width,
            y = size.center.y
        ),
        strokeWidth = size.height,
    )
    drawRect(
        color = MozillaColor.Red,
        topLeft = Offset(
            x = endThumbOffset,
            y = 0f
        ),
        size = Size(thumbWidth.value, size.height)
    )
}