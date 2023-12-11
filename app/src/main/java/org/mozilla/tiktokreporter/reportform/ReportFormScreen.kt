package org.mozilla.tiktokreporter.reportform

import android.Manifest
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFramePercent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ScreenRecorderService
import org.mozilla.tiktokreporter.TikTokReporterError
import org.mozilla.tiktokreporter.UploadRecordingService
import org.mozilla.tiktokreporter.common.TabModelType
import org.mozilla.tiktokreporter.common.formcomponents.formComponentsItems
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTabRow
import org.mozilla.tiktokreporter.ui.components.MozillaTextField
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.PrimaryButton
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.components.dialog.DialogState
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.util.CollectWithLifecycle
import org.mozilla.tiktokreporter.util.UiText
import org.mozilla.tiktokreporter.util.onSdkVersionAndDown
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportFormScreen(
    viewModel: ReportFormScreenViewModel = hiltViewModel(),
    onGoToReportSubmittedScreen: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToStudies: () -> Unit,
    onGoToEditVideo: () -> Unit,
    onGoBack: () -> Unit
) {

    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.checkVideoExists(context)
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val mediaProjectionPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->

            if (result.resultCode == ComponentActivity.RESULT_OK) {
                Intent(context.applicationContext, ScreenRecorderService::class.java).also {
                    it.action = ScreenRecorderService.Actions.START.toString()
                    it.putExtra("activityResult", result)

                    context.startForegroundService(it)
                }
            }
        }
    )

    val writeExternalStoragePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val writeExternalStoragePermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
            if (wasGranted) {
                mediaProjectionPermissionLauncher.launch(
                    context.getSystemService(MediaProjectionManager::class.java)
                        .createScreenCaptureIntent()
                )
            }
        }

    val notificationsPermissionState = onSdkVersionAndUp(Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    }
    val notificationsPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
            if (wasGranted) {
                mediaProjectionPermissionLauncher.launch(
                    context.getSystemService(MediaProjectionManager::class.java)
                        .createScreenCaptureIntent()
                )
            }
        }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    DialogContainer { dialogState ->

        CollectWithLifecycle(viewModel.uiAction) { action ->
            when (action) {
                is ReportFormScreenViewModel.UiAction.StartUploadRecordingService -> {
                    Intent(context.applicationContext, UploadRecordingService::class.java).also {
                        it.action = UploadRecordingService.Actions.START.toString()
                        it.putExtra("recordingUri", action.recordingUri.toString())

                        context.startForegroundService(it)
                    }
                }
                ReportFormScreenViewModel.UiAction.StopUploadRecordingService -> {
                    Intent(context.applicationContext, UploadRecordingService::class.java).also {
                        it.action = UploadRecordingService.Actions.STOP.toString()

                        context.startService(it)
                    }

                    onGoToReportSubmittedScreen()
                }
                is ReportFormScreenViewModel.UiAction.ShowStudyNotActive -> {
                    dialogState.value = DialogState.MessageDialog(
                        title = UiText.StringResource(R.string.dialog_title_inactive_study),
                        message = UiText.StringResource(R.string.dialog_message_inactive_study),
                        positiveButtonText = UiText.StringResource(R.string.settings),
                        onPositive = onGoToStudies,
                        negativeButtonText = UiText.StringResource(R.string.not_now),
                        onNegative = onGoBack,
                        onDismissRequest = { dialogState.value = DialogState.Nothing }
                    )
                }
                ReportFormScreenViewModel.UiAction.GoToReportSubmittedScreen -> {
                    onGoToReportSubmittedScreen()
                }
                is ReportFormScreenViewModel.UiAction.ShowError -> {
                    when (action.error) {
                        // internet connection / server unresponsive / server error
                        is TikTokReporterError.NetworkError, is TikTokReporterError.ServerError, is TikTokReporterError.UnknownError -> {
                            dialogState.value = DialogState.ErrorDialog(
                                title = UiText.StringResource(R.string.error_title_general),
                                message = UiText.StringResource(R.string.error_message_general),
                                drawable = R.drawable.error_cat,
                                actionText = UiText.StringResource(R.string.button_refresh),
                                action = { } // TODO: refresh
                            )
                        }
                    }
                }
                ReportFormScreenViewModel.UiAction.ShowFetchStudyError -> {
                    dialogState.value = DialogState.ErrorDialog(
                        title = UiText.StringResource(R.string.error_title_general),
                        message = UiText.StringResource(R.string.error_message_general),
                        drawable = R.drawable.error_cat,
                        actionText = UiText.StringResource(R.string.button_refresh),
                        action = { } // TODO: refresh
                    )
                }
            }
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            ReportFormScreenContent(
                state = state,
                onFormFieldValueChanged = viewModel::onFormFieldValueChanged,
                onRecordSessionCommentsChanged = viewModel::onRecordSessionCommentsChanged,
                onTabSelected = viewModel::onTabSelected,
                onSubmitReport = viewModel::onSubmitReport,
                onCancelReport = {
                    dialogState.value = DialogState.MessageDialog(
                        title = UiText.StringResource(R.string.dialog_title_cancel_report),
                        message = UiText.StringResource(R.string.dialog_message_cancel_report),
                        positiveButtonText = UiText.StringResource(R.string.delete),
                        onPositive = {
                            viewModel.onCancelReport()
                            dialogState.value = DialogState.Nothing
                        },
                        negativeButtonText = UiText.StringResource(R.string.keep),
                        onNegative = {
                            dialogState.value = DialogState.Nothing
                        },
                        onDismissRequest = {
                            dialogState.value = DialogState.Nothing
                        }
                    )
                },
                onGoToSettings = onGoToSettings,
                onStartRecording = {
                    onSdkVersionAndUp(Build.VERSION_CODES.TIRAMISU) {

                        when (notificationsPermissionState?.status) {
                            PermissionStatus.Granted -> {
                                mediaProjectionPermissionLauncher.launch(
                                    context.getSystemService(MediaProjectionManager::class.java)
                                        .createScreenCaptureIntent()
                                )
                            }

                            else -> {
                                if (notificationsPermissionState?.status?.shouldShowRationale == true) {
                                    dialogState.value = DialogState.MessageDialog(
                                        title = UiText.StringResource(R.string.dialog_title_notification_permission),
                                        message = UiText.StringResource(R.string.dialog_message_notification_permission),
                                        positiveButtonText = UiText.StringResource(R.string.settings),
                                        onPositive = {
                                            dialogState.value = DialogState.Nothing
                                            val intent = Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", context.packageName, null)
                                            )
                                            context.startActivity(intent)
                                        }
                                    )
                                } else {
                                    notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        }

                    } ?: onSdkVersionAndDown(Build.VERSION_CODES.Q) {
                        when (writeExternalStoragePermissionState.status) {
                            PermissionStatus.Granted -> {
                                mediaProjectionPermissionLauncher.launch(
                                    context.getSystemService(MediaProjectionManager::class.java)
                                        .createScreenCaptureIntent()
                                )
                            }

                            else -> {
                                if (notificationsPermissionState?.status?.shouldShowRationale == true) {
                                    dialogState.value = DialogState.MessageDialog(
                                        title = UiText.StringResource(R.string.dialog_title_write_external_storage_permission),
                                        message = UiText.StringResource(R.string.dialog_message_write_external_storage_permission),
                                        positiveButtonText = UiText.StringResource(R.string.got_it),
                                        onPositive = {
                                            dialogState.value = DialogState.Nothing
                                            val intent = Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", context.packageName, null)
                                            )
                                            context.startActivity(intent)
                                        }
                                    )
                                } else {
                                    writeExternalStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    } ?: mediaProjectionPermissionLauncher.launch(
                        context.getSystemService(MediaProjectionManager::class.java)
                            .createScreenCaptureIntent()
                    )
                },
                onStopRecording = {
                    Intent(context.applicationContext, ScreenRecorderService::class.java).also {
                        it.action = ScreenRecorderService.Actions.STOP.toString()
                        context.startService(it)
                    }
                },
                onGoToEditVideo = onGoToEditVideo,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ReportFormScreenContent(
    state: ReportFormScreenViewModel.State,
    onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit,
    onRecordSessionCommentsChanged: (String) -> Unit,
    onTabSelected: (tabIndex: Int) -> Unit,
    onSubmitReport: () -> Unit,
    onCancelReport: () -> Unit,
    onGoToSettings: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onGoToEditVideo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MozillaScaffold(
        modifier = modifier,
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                action = {
                    IconButton(onClick = onGoToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    horizontal = MozillaDimension.M,
                    vertical = MozillaDimension.L
                ),
                verticalArrangement = Arrangement.spacedBy(MozillaDimension.L),
                content = {
                    item {
                        MozillaTabRow(
                            modifier = modifier,
                            tabs = state.tabs.map {
                                when (it) {
                                    TabModelType.ReportLink -> stringResource(id = R.string.report_link)
                                    TabModelType.RecordSession -> stringResource(id = R.string.record_session)
                                }
                            },
                            onTabSelected = onTabSelected,
                            selectedTabIndex = state.selectedTab?.second ?: 0
                        )
                    }

                    when (state.selectedTab?.first) {
                        TabModelType.ReportLink -> {
                            formComponentsItems(
                                formFields = state.formFields,
                                onFormFieldValueChanged = onFormFieldValueChanged
                            )
                        }

                        TabModelType.RecordSession -> {
                            recordSessionItems(
                                isRecording = state.isRecording,
                                video = state.video,
                                showSubmitNoVideoError = state.showSubmitNoVideoError,
                                comments = state.recordSessionComments,
                                onCommentsChanged = onRecordSessionCommentsChanged,
                                onStartRecording = onStartRecording,
                                onStopRecording = onStopRecording,
                                onGoToEditVideo = onGoToEditVideo
                            )
                        }

                        null -> Unit
                    }
                }
            )

            FormButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MozillaDimension.M,
                        vertical = MozillaDimension.L
                    ),
                onSubmitReport = onSubmitReport,
                onCancelReport = onCancelReport
            )
        }
    }
}

private fun LazyListScope.recordSessionItems(
    isRecording: Boolean,
    showSubmitNoVideoError: Boolean,
    video: ReportFormScreenViewModel.VideoModel?,
    comments: String,
    onCommentsChanged: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onGoToEditVideo: () -> Unit
) {
    item {
        val text = if (video == null) stringResource(id = R.string.start_recording_session)
        else stringResource(id = R.string.recording_session_available)
        Text(
            modifier = Modifier.fillParentMaxWidth(),
            text = text,
            style = MozillaTypography.Body2
        )
    }

    when {
        video == null && !isRecording -> {
            item {
                SecondaryButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    text = stringResource(id = R.string.button_record_tiktok_session),
                    onClick = onStartRecording
                )
            }
        }

        video == null && isRecording -> {
            item {
                SecondaryButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    text = stringResource(id = R.string.button_stop_recording),
                    onClick = onStopRecording
                )
            }
        }

        else -> {
            item {
                VideoEntry(
                    modifier = Modifier.fillParentMaxWidth(),
                    video = video!!
                )
            }

            item {
                SecondaryButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    text = stringResource(id = R.string.button_trim_recording),
                    onClick = onGoToEditVideo
                )
            }
        }
    }

    if (showSubmitNoVideoError) {
        item {
            Text(
                text = stringResource(id = R.string.error_message_no_recording_available),
                style = MozillaTypography.Body2,
                color = MozillaColor.Error
            )
        }
    }

    item {
        MozillaTextField(
            modifier = Modifier.fillParentMaxWidth(),
            text = comments,
            onTextChanged = onCommentsChanged,
            label = stringResource(id = R.string.text_field_label_comments_optional),
            maxLines = 5,
            multiline = true
        )
    }
}

@Composable
private fun VideoEntry(
    modifier: Modifier = Modifier,
    video: ReportFormScreenViewModel.VideoModel,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MozillaDimension.L),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(video.uri)
                .videoFramePercent(.5)
                .build(),
            imageLoader = ImageLoader.Builder(context)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .build(),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.XXS)
        ) {
            Text(text = stringResource(id = R.string.label_recorded_video))
            Text(text = stringResource(id = R.string.duration_seconds, video.duration))
            Text(text = stringResource(id = R.string.recorded_on_at, video.date, video.time))
        }
    }
}

@Composable
private fun FormButtons(
    modifier: Modifier = Modifier,
    onSubmitReport: (() -> Unit)? = null,
    onCancelReport: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MozillaDimension.S)
    ) {
        onSubmitReport?.let {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.button_submit_report),
                onClick = it,
                isPrimaryVariant = true
            )
        }
        onCancelReport?.let {
            SecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.button_cancel_report),
                onClick = it
            )
        }
    }
}