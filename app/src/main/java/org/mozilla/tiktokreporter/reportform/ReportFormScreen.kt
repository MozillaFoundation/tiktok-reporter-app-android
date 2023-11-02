package org.mozilla.tiktokreporter.reportform

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.data.model.FormField
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaDropdown
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaSlider
import org.mozilla.tiktokreporter.ui.components.MozillaTabRow
import org.mozilla.tiktokreporter.ui.components.MozillaTextField
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.PrimaryButton
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun ReportFormScreen(
    viewModel: ReportFormScreenViewModel = hiltViewModel(),
    onGoToReportSubmittedScreen: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
    onGoToAppPurpose: () -> Unit
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
                onGoToTermsAndConditions = onGoToTermsAndConditions,
                onGoToPrivacyPolicy = onGoToPrivacyPolicy,
                onGoToAppPurpose = onGoToAppPurpose,
                modifier = Modifier.fillMaxSize()
            )
        }

        when (action) {
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
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
    onGoToAppPurpose: () -> Unit,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                                ReportFormScreenViewModel.TabModelType.ReportLink -> " Report a Link"
                                ReportFormScreenViewModel.TabModelType.RecordSession -> "Record a session"
                            }
                        },
                        onTabSelected = onTabSelected,
                        selectedTabIndex = state.selectedTabIndex
                    )
                }

                if (state.selectedTabIndex == 0) {
                    reportLinkItems(
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
                item {
                    FormFooter(
                        modifier = Modifier.fillParentMaxWidth(),
                        onSubmitReport = onSubmitReport,
                        onCancelReport = {

                        },
                        onGoToAppPurpose = onGoToAppPurpose,
                        onGoToTermsAndConditions = onGoToTermsAndConditions,
                        onGoToPrivacyPolicy = onGoToPrivacyPolicy,
                    )
                }
            }
        )
    }
}

private fun LazyListScope.reportLinkItems(
    formFields: List<ReportFormScreenViewModel.FormFieldUiComponent>,
    onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit
) {
    items(formFields) { field ->
        when (val formField = field.formField) {
            is FormField.TextField -> {
                FormTextField(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = formField,
                    value = field.value as String,
                    onTextChanged = {
                        onFormFieldValueChanged(formField.id, it)
                    }
                )
            }

            is FormField.DropDown -> {
                FormDropDown(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = formField,
                    value = field.value as String,
                    onOptionChanged = {
                        onFormFieldValueChanged(formField.id, it)
                    }
                )
            }

            is FormField.Slider -> {
                FormSlider(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = formField,
                    sliderPosition = field.value as Int,
                    onValueChanged = {
                        onFormFieldValueChanged(formField.id, it)
                    }
                )
            }

            else -> Unit
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
private fun FormTextField(
    field: FormField.TextField,
    value: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaTextField(
            modifier = Modifier.fillMaxWidth(),
            text = value,
            onTextChanged = onTextChanged,
            label = field.label,
            placeholder = field.placeholder,
            maxLines = field.maxLines,
            multiline = field.multiline,
        )
    }
}

@Composable
private fun FormDropDown(
    field: FormField.DropDown,
    value: String,
    onOptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaDropdown(
            modifier = Modifier.fillMaxWidth(),
            options = field.options.map { it.title },
            onOptionSelected = { index, option ->
                onOptionChanged(option)
            },
            text = value,
            label = field.label,
            placeholder = field.placeholder,
        )
    }
}

@Composable
private fun FormSlider(
    field: FormField.Slider,
    sliderPosition: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (field.label.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.label,
                style = MozillaTypography.Body1
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaSlider(
            modifier = Modifier.fillMaxWidth(),
            sliderPosition = sliderPosition,
            max = field.max,
            step = field.step,
            onValueChanged = onValueChanged,
            leftLabel = field.leftLabel,
            rightLabel = field.rightLabel
        )
    }
}

@Composable
private fun FormFooter(
    modifier: Modifier = Modifier,
    onSubmitReport: () -> Unit,
    onCancelReport: () -> Unit,
    onGoToAppPurpose: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MozillaDimension.XL),
    ) {
        FormButtons(
            modifier = Modifier.fillMaxWidth(),
            onSubmitReport = onSubmitReport,
            onCancelReport = onCancelReport
        )

        FormLinks(
            modifier = Modifier.fillMaxWidth(),
            onGoToAppPurpose = onGoToAppPurpose,
            onGoToTermsAndConditions = onGoToTermsAndConditions,
            onGoToPrivacyPolicy = onGoToPrivacyPolicy
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

@Composable
private fun FormLinks(
    modifier: Modifier = Modifier,
    onGoToAppPurpose: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MozillaDimension.M),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.clickable {
                onGoToAppPurpose()
            },
            text = "TikTok Reporter Purpose",
            style = MozillaTypography.Body2,
            color = MozillaColor.TextColor
        )
        ConstraintLayout {
            val (termsRef, dividerRef, policyRef) = createRefs()

            Text(
                modifier = Modifier
                    .constrainAs(termsRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        onGoToTermsAndConditions()
                    },
                text = "Terms & Conditions",
                style = MozillaTypography.Body2,
                color = MozillaColor.TextColor
            )

            Box(
                modifier = Modifier
                    .constrainAs(dividerRef) {
                        start.linkTo(termsRef.end, margin = MozillaDimension.XXS)
                        top.linkTo(parent.top)
                        end.linkTo(policyRef.start, margin = MozillaDimension.XXS)
                        bottom.linkTo(parent.bottom)

                        height = Dimension.fillToConstraints
                        width = Dimension.value(1.dp)
                    }
                    .background(MozillaColor.TextColor)
            )
            Text(
                modifier = Modifier
                    .constrainAs(policyRef) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        onGoToPrivacyPolicy()
                    },
                text = "Privacy Policy",
                style = MozillaTypography.Body2,
                color = MozillaColor.TextColor
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun FormLinksPreview() {
    TikTokReporterTheme {
        FormLinks(
            modifier = Modifier.fillMaxWidth(),
            onGoToAppPurpose = {},
            onGoToTermsAndConditions = {},
            onGoToPrivacyPolicy = {}
        )
    }
}