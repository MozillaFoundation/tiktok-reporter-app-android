package org.mozilla.tiktokreporter.reportform

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun ReportFormScreen(
    viewModel: ReportFormScreenViewModel = hiltViewModel(),
    onGoToReportSubmittedScreen: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToStudies: () -> Unit,
) {
    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val action = state.action?.get()
        val isLoading = action is ReportFormScreenViewModel.UiAction.ShowLoading

        if (isLoading) {
            LoadingScreen()
        } else {
            ReportFormScreenContent(
                state = state,
                onFormFieldValueChanged = viewModel::onFormFieldValueChanged,
                onTabSelected = viewModel::onTabSelected,
                onSubmitReport = viewModel::onSubmitReport,
                onGoToSettings = onGoToSettings,
                modifier = Modifier.fillMaxSize()
            )
        }

        when (action) {
            is ReportFormScreenViewModel.UiAction.ShowStudyNotActive -> {
                dialogState.value = DialogState.Message(
                    title = "Select another study",
                    message = "The study you were participating into has ended. Please select another study to join.",
                    positiveButtonText = "Settings",
                    onPositive = onGoToStudies,
                    negativeButtonText = "Not now",
                    onNegative = {

                    },
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
    onTabSelected: (tabIndex: Int) -> Unit,
    onSubmitReport: () -> Unit,
    onGoToSettings: () -> Unit,
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
            modifier = Modifier.fillMaxSize()
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
                                    TabModelType.ReportLink -> " Report a Link"
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
                            isRecording = false,
                            onStartRecording = {

                            }
                        )
                    }
                }
            )

            FormButtons(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        horizontal = MozillaDimension.M,
                        vertical = MozillaDimension.L
                    ),
                onSubmitReport = onSubmitReport,
                onCancelReport = {

                }
            )
        }
    }
}

private fun LazyListScope.recordSessionItems(
    videoAvailable: Boolean = false,
    isRecording: Boolean,
    onStartRecording: () -> Unit
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
                onClick = onStartRecording
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
            text = "",
            onTextChanged = { },
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