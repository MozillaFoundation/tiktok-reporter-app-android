package org.mozilla.tiktokreporter.email

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.TikTokReporterError
import org.mozilla.tiktokreporter.common.FormFieldError
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.formcomponents.formComponentsItems
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
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

enum class EmailScreenMode {
    ONBOARDING,
    SETTINGS_UPDATES,
    SETTINGS_DATA_HANDLING
}

@Composable
fun EmailScreen(
    viewModel: EmailScreenViewModel = hiltViewModel(), mode: EmailScreenMode = EmailScreenMode.ONBOARDING, onNextScreen: () -> Unit, onNavigateBack: () -> Unit
) {
    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        CollectWithLifecycle(flow = viewModel.uiAction, onCollect = { action ->
            when (action) {
                EmailScreenViewModel.UiAction.GoToReportForm -> onNextScreen()
                EmailScreenViewModel.UiAction.EmailSaved -> {
                    if (mode == EmailScreenMode.ONBOARDING) onNextScreen()
                    else onNavigateBack()
                }

                is EmailScreenViewModel.UiAction.ShowError -> {
                    when (action.error) {
                        // internet connection / server unresponsive / server error
                        is TikTokReporterError.NetworkError -> {
                            dialogState.value = DialogState.ErrorDialog(title = UiText.StringResource(R.string.error_title_internet),
                                drawable = R.drawable.error_cat,
                                actionText = UiText.StringResource(R.string.button_refresh),
                                action = {
                                    viewModel.refresh()
                                    dialogState.value = DialogState.Nothing
                                })
                        }

                        is TikTokReporterError.ServerError -> {
                            dialogState.value = DialogState.ErrorDialog(title = UiText.StringResource(R.string.error_title_general),
                                message = UiText.StringResource(R.string.error_message_general),
                                drawable = R.drawable.error_cat,
                                actionText = UiText.StringResource(R.string.button_refresh),
                                action = {
                                    viewModel.refresh()
                                    dialogState.value = DialogState.Nothing
                                })
                        }

                        is TikTokReporterError.UnknownError -> {
                            dialogState.value = DialogState.ErrorDialog(
                                title = UiText.StringResource(R.string.error_title_general),
                                message = UiText.StringResource(R.string.error_message_general),
                                drawable = R.drawable.error_cat
                            )
                        }
                    }
                }
            }
        })

        if (isLoading) {
            LoadingScreen()
        } else {
            EmailScreenContent(
                state = state,
                onFormFieldValueChanged = viewModel::onFormFieldValueChanged,
                onNavigateBack = onNavigateBack,
                onSaveEmail = if (mode === EmailScreenMode.SETTINGS_DATA_HANDLING) viewModel::onSaveDataHandlingEmail else viewModel::onSaveEmail,
                onNextScreen = onNextScreen,
                mode = mode,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun EmailScreenContent(
    state: EmailScreenViewModel.State,
    onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit,
    onNavigateBack: () -> Unit,
    onSaveEmail: () -> Unit,
    onNextScreen: () -> Unit,
    mode: EmailScreenMode,
    modifier: Modifier = Modifier
) {
    MozillaScaffold(modifier = modifier, topBar = if (mode == EmailScreenMode.ONBOARDING) null else {
        {
            MozillaTopAppBar(modifier = Modifier.fillMaxWidth(), navItem = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = "", tint = MozillaColor.TextColor
                    )
                }
            })
        }
    }) { innerPadding ->
        val scrollState = rememberScrollState()
        var headingText = stringResource(R.string.sign_up_for_updates)
        var formFields = state.formFields
        if (mode == EmailScreenMode.SETTINGS_DATA_HANDLING) {
            headingText = stringResource(R.string.email_for_data_download)
            formFields = state.dataFormFields
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        PaddingValues(
                            horizontal = MozillaDimension.M, vertical = MozillaDimension.L
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = headingText,
                        style = MozillaTypography.H3
                    )
                    formComponentsItems(
                        formFields = formFields, onFormFieldValueChanged = onFormFieldValueChanged
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MozillaDimension.M)
            ) {
                OnboardingFormButtons(modifier = Modifier.fillMaxWidth(), nextButton = {
                    if (mode != EmailScreenMode.SETTINGS_UPDATES) {
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(), text = stringResource(id = R.string.save), onClick = onSaveEmail
                        )
                    }
                }, skipButton = {
                    if (mode != EmailScreenMode.SETTINGS_DATA_HANDLING) {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (mode == EmailScreenMode.ONBOARDING) stringResource(id = R.string.skip) else stringResource(
                                id = R.string.save
                            ),
                            onClick = if (mode == EmailScreenMode.ONBOARDING) onNextScreen else onSaveEmail
                        )
                    }
                })
            }
        }
    }
}


@Composable
private fun OnboardingFormButtons(
    modifier: Modifier = Modifier,
    nextButton: (@Composable () -> Unit)? = null,
    skipButton: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
    ) {
        nextButton?.let { it() }
        skipButton?.let { it() }
    }
}