package org.mozilla.tiktokreporter.email

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.TikTokReporterError
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
    val context = LocalContext.current
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
                EmailScreenViewModel.UiAction.EmailRemoved -> {
                    Toast.makeText(context, R.string.email_removed, Toast.LENGTH_SHORT).show()
                }
                EmailScreenViewModel.UiAction.ShowDataDownloaded -> {
                    Toast.makeText(context, R.string.toast_download_my_data, Toast.LENGTH_SHORT).show()
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
                onRemoveEmail = viewModel::onRemoveEmail,
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
    onFormFieldValueChanged: (formFieldId: String, value: Any, mode: EmailScreenMode) -> Unit,
    onNavigateBack: () -> Unit,
    onSaveEmail: () -> Unit,
    onRemoveEmail: (mode: EmailScreenMode) -> Unit,
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
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                    })
                }
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
                        formFields = formFields, onFormFieldValueChanged = fun(formFieldId, value) {
                            onFormFieldValueChanged(formFieldId, value, mode)
                        }
                    )
                    MarkdownText(
                        modifier = Modifier.padding(top = MozillaDimension.L),
                        markdown = stringResource(R.string.email_policy_markdown),
                        style = MozillaTypography.Body2,
                        linkColor = Color.Blue
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
                    } else if (state.userEmail.isNotEmpty()) {
                        MarkdownText(
                            modifier = Modifier.fillMaxWidth().padding(PaddingValues(
                                vertical = MozillaDimension.M
                            )),
                            markdown = stringResource(R.string.email_remove_description), style = MozillaTypography.Body2, linkColor = Color.Blue
                        )
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.email_remove),
                            onClick = fun() {
                                onRemoveEmail(mode)
                            }
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