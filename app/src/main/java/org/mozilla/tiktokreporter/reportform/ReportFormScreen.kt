package org.mozilla.tiktokreporter.reportform

import android.Manifest
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
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
                val intent = Intent(context.applicationContext, ScreenRecorderService::class.java).also {
                    it.action = ScreenRecorderService.Actions.START.toString()
                    it.putExtra("activityResult", result)

                }

                context.startService(intent)
            }
        }
    )

    var userRequestedRecording by remember { mutableStateOf(false) }
    val notificationsPermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(
        key1 = notificationsPermissionState.status,
        key2 = userRequestedRecording,
        block = {
            if (userRequestedRecording && notificationsPermissionState.status.isGranted) {
                mediaProjectionPermissionLauncher.launch(
                    context.getSystemService(MediaProjectionManager::class.java).createScreenCaptureIntent()
                )
            }
        }
    )

    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

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
                    userRequestedRecording = true

                    onSdkVersionAndUp(Build.VERSION_CODES.TIRAMISU) {
                        if (notificationsPermissionState.status.isGranted) {

                        } else if (notificationsPermissionState.status.shouldShowRationale) {

                            dialogState.value = DialogState.Message(
                                title = "Notifications permission required",
                                message = "Notification permission required",
                                positiveButtonText = "Got it",
                                onPositive = {
                                    notificationsPermissionState.launchPermissionRequest()
                                    dialogState.value = DialogState.Nothing
                                }
                            )
                        } else {
                            notificationsPermissionState.launchPermissionRequest()
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

        when (state.action?.get()) {
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
                            selectedTabIndex = state.selectedTabIndex
                        )
                    }

                    if (state.selectedTabIndex == 0) {
                        formComponentsItems(
                            formFields = state.formFields,
                            onFormFieldValueChanged = onFormFieldValueChanged
                        )
                    } else {
                        recordSessionItems(
                            isRecording = state.isRecording,
                            comments = state.recordSessionComments,
                            onCommentsChanged = onRecordSessionCommentsChanged,
                            onStartRecording = onStartRecording,
                            onStopRecording = onStopRecording
                        )
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
    videoAvailable: Boolean = false,
    isRecording: Boolean,
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

    if (!videoAvailable) {
        item {
            SecondaryButton(
                modifier = Modifier.fillParentMaxWidth(),
                text = if (isRecording) "Stop Recording" else "Record My TikTok Session",
                onClick = if (isRecording) onStopRecording else onStartRecording
            )
        }
    } else {
        item {
            Text(
                modifier = Modifier.fillParentMaxWidth(),
                text = "Video thumbnail and info"
            )
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