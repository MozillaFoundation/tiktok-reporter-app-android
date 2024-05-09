package org.mozilla.tiktokreporter.apppolicy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.TikTokReporterError
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

@Composable
fun AppPolicyScreen(
    viewModel: AppPolicyScreenViewModel = hiltViewModel(),
    isForOnboarding: Boolean = true,
    onGoToStudies: () -> Unit = { },
    onGoToStudyOnboarding: () -> Unit = { },
    onNavigateBack: () -> Unit = { },
) {
    DialogContainer(
        modifier = Modifier.fillMaxSize()
    ) { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        if (isLoading) {
            LoadingScreen()
        } else {
            AppPolicyScreenContent(
                state = state,
                isForOnboarding = isForOnboarding,
                onNavigateBack = onNavigateBack,
                onAgree = viewModel::acceptTerms,
                onDisagree = {
                    if (isForOnboarding) {
                        dialogState.value =
                            DialogState.MessageDialog(title = UiText.StringResource(R.string.dialog_title_review_terms_and_conditions),
                                message = UiText.StringResource(R.string.dialog_message_review_terms_and_conditions),
                                negativeButtonText = UiText.StringResource(R.string.got_it),
                                onNegative = {
                                    dialogState.value = DialogState.Nothing
                                },
                                onDismissRequest = {
                                    dialogState.value = DialogState.Nothing
                                })
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        CollectWithLifecycle(viewModel.uiAction) { action ->
            when (action) {
                AppPolicyScreenViewModel.UiAction.OnGoToStudies -> onGoToStudies()
                AppPolicyScreenViewModel.UiAction.OnGoToStudyOnboarding -> onGoToStudyOnboarding()

                is AppPolicyScreenViewModel.UiAction.ShowError -> {

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

                AppPolicyScreenViewModel.UiAction.ShowNoPolicyFound -> Unit
            }
        }
    }
}

@Composable
private fun AppPolicyScreenContent(
    state: AppPolicyScreenViewModel.State,
    isForOnboarding: Boolean,
    onNavigateBack: () -> Unit,
    onAgree: () -> Unit,
    onDisagree: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val buttonsEnabled by remember {
        derivedStateOf {
            !scrollState.canScrollForward
        }
    }

    MozillaScaffold(modifier = modifier) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), contentPadding = PaddingValues(
                    horizontal = MozillaDimension.M, vertical = MozillaDimension.L
                ), state = scrollState
            ) {
                if (state.title.isNotEmpty() && state.content.isNotEmpty()) {
                    item {
                        MarkdownText(
                            markdown = state.title, style = MozillaTypography.H3
                        )
                        Spacer(modifier = Modifier.height(MozillaDimension.L))
                    }
                    if (state.subtitle.trim().isNotEmpty()) {
                        item {
                            MarkdownText(
                                markdown = state.subtitle, style = MozillaTypography.H5
                            )
                            Spacer(modifier = Modifier.height(MozillaDimension.M))
                        }
                    }
                    item {
                        MarkdownText(
                            markdown = state.content, style = MozillaTypography.Body2, linkColor = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(MozillaDimension.L))
                    }
                }
            }

            if (isForOnboarding) {
                AppPolicyButtons(modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MozillaDimension.M, vertical = MozillaDimension.L
                    ), agreeButton = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.i_agree),
                        onClick = onAgree,
                        enabled = buttonsEnabled
                    )
                }, disagreeButton = {
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.i_disagree),
                        onClick = onDisagree,
                        enabled = buttonsEnabled
                    )
                })
            }
        }
    }
}

@Composable
private fun AppPolicyButtons(
    modifier: Modifier = Modifier,
    agreeButton: (@Composable () -> Unit)? = null,
    disagreeButton: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
    ) {
        agreeButton?.let { it() }
        disagreeButton?.let { it() }
    }
}