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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.mozilla.tiktokreporter.ScreenRecorderService
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
import org.mozilla.tiktokreporter.util.collectWithLifecycle
import org.mozilla.tiktokreporter.util.onSdkVersionAndDown
import org.mozilla.tiktokreporter.util.onSdkVersionAndUp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportFormScreen(
    viewModel: ReportFormScreenViewModel = hiltViewModel(),
    onGoToReportSubmittedScreen: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToStudies: () -> Unit,
    onGoBack: () -> Unit
) {
    val context = LocalContext.current
    val mediaProjectionPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->

            if (result.resultCode == ComponentActivity.RESULT_OK) {
                Intent(context.applicationContext, ScreenRecorderService::class.java).also {
                    it.action = ScreenRecorderService.Actions.START.toString()
                    it.putExtra("activityResult", result)

                    context.startService(it)
                }
            }
        }
    )

    val writeExternalStoragePermissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val writeExternalStoragePermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            mediaProjectionPermissionLauncher.launch(
                context.getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
            )
        }
    }

    val notificationsPermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val notificationsPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            mediaProjectionPermissionLauncher.launch(
                context.getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
            )
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    DialogContainer { dialogState ->

        viewModel.uiAction.collectWithLifecycle { action ->
            when (action) {
                is ReportFormScreenViewModel.UiAction.ShowStudyNotActive -> {
                    dialogState.value = DialogState.Message(
                        title = "Select another study",
                        message = "The study you were participating into has ended. Please select another study to join.",
                        positiveButtonText = "Settings",
                        onPositive = onGoToStudies,
                        negativeButtonText = "Not now",
                        onNegative = onGoBack,
                        onDismissRequest = { dialogState.value = DialogState.Nothing }
                    )
                }
                is ReportFormScreenViewModel.UiAction.GoToReportSubmittedScreen -> onGoToReportSubmittedScreen()
                else -> Unit
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
                    dialogState.value = DialogState.Message(
                        title = "Cancel report?",
                        message = "Are you sure you want to cancel the report? All the data entered will be deleted.",
                        positiveButtonText = "Delete",
                        onPositive = {
                            viewModel.onCancelReport()
                            dialogState.value = DialogState.Nothing
                        },
                        negativeButtonText = "Keep",
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

                        when (notificationsPermissionState.status) {
                            PermissionStatus.Granted -> {
                                mediaProjectionPermissionLauncher.launch(
                                    context.getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
                                )
                            }

                            else -> {
                                if (notificationsPermissionState.status.shouldShowRationale) {
                                    dialogState.value = DialogState.Message(
                                        title = "Notifications permission required",
                                        message = "Notification permission required",
                                        positiveButtonText = "Got it",
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
                                    context.getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
                                )
                            }

                            else -> {
                                if (notificationsPermissionState.status.shouldShowRationale) {
                                    dialogState.value = DialogState.Message(
                                        title = "Write external storage permission required",
                                        message = "Write external storage permission required",
                                        positiveButtonText = "Got it",
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
                        context.getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
                    )
                },
                onStopRecording = {
                    Intent(context.applicationContext, ScreenRecorderService::class.java).also {
                        it.action = ScreenRecorderService.Actions.STOP.toString()
                        context.startService(it)
                    }
                },
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
                                    TabModelType.ReportLink -> "Report a Link"
                                    TabModelType.RecordSession -> "Record a session"
                                }
                            },
                            onTabSelected = onTabSelected,
                            selectedTabIndex = state.selectedTab?.second ?: 0
                        )
                    }

                    when(state.selectedTab?.first) {
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
                                comments = state.recordSessionComments,
                                onCommentsChanged = onRecordSessionCommentsChanged,
                                onStartRecording = onStartRecording,
                                onStopRecording = onStopRecording
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
    video: ReportFormScreenViewModel.VideoModel?,
    comments: String,
    onCommentsChanged: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
) {
    item {
        Text(
            modifier = Modifier.fillParentMaxWidth(),
            text = "Start recording a TikTok session by pressing the button below and then go and browse TikTok. Once the recording is stopped you can submit this form.",
            style = MozillaTypography.Body2
        )
    }

    when {
        video == null && !isRecording -> {
            item {
                SecondaryButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    text = "Record My TikTok Session",
                    onClick = onStartRecording
                )
            }
        }

        video == null && isRecording -> {
            item {
                SecondaryButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    text = "Stop Recording",
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
        }
    }

    item {
        MozillaTextField(
            modifier = Modifier.fillParentMaxWidth(),
            text = comments,
            onTextChanged = onCommentsChanged,
            label = "Comments (optional)",
            maxLines = 5,
            multiline = true
        )
    }
}

@Composable
private fun VideoEntry(
    modifier: Modifier = Modifier,
    video: ReportFormScreenViewModel.VideoModel
) {
    val imageBitmap = video.thumbnail?.asImageBitmap()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MozillaDimension.L),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageBitmap != null) {
            Image(
                modifier = Modifier.size(80.dp),
                bitmap = imageBitmap,
                contentDescription = null
            )
        } else {
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.XXS)
        ) {
            Text(text = "Recorded Video")
            Text(text = "Duration: ${video.duration} seconds")
            Text(text = "Recorded on: ${video.date} at ${video.time}")
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
                text = "Submit Report",
                onClick = it,
                isPrimaryVariant = true
            )
        }
        onCancelReport?.let {
            SecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Cancel Report",
                onClick = it
            )
        }
    }
}