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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.R
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
import org.mozilla.tiktokreporter.util.emptyCallback

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

        when (val action = state.action?.get()) {
            AppPolicyScreenViewModel.UiAction.OnGoToStudies -> onGoToStudies()
            AppPolicyScreenViewModel.UiAction.OnGoToStudyOnboarding -> onGoToStudyOnboarding()

            // TODO: replace with general error screen
            is AppPolicyScreenViewModel.UiAction.ShowMessage -> {
                dialogState.value = DialogState.Message(
                    title = "Alert",
                    message = action.message,
                    positiveButtonText = "Got it",
                    onPositive = { dialogState.value = DialogState.Nothing },
                    onDismissRequest = { dialogState.value = DialogState.Nothing }
                )
            }
            AppPolicyScreenViewModel.UiAction.ShowNoPolicyFound -> Unit
            null -> Unit
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            AppPolicyScreenContent(
                state = state,
                isForOnboarding = isForOnboarding,
                onNavigateBack = onNavigateBack,
                onAgree = viewModel::acceptTerms,
                onDisagree = if (isForOnboarding) {
                    {
                        dialogState.value = DialogState.MessageRes(
                            title = R.string.dialog_title_review_terms_and_conditions,
                            message = R.string.dialog_message_review_terms_and_conditions,
                            negativeButtonText = R.string.got_it,
                            onNegative = {
                                dialogState.value = DialogState.Nothing
                            },
                            onDismissRequest = {
                                dialogState.value = DialogState.Nothing
                            }
                        )
                    }
                } else emptyCallback,
                modifier = Modifier.fillMaxSize()
            )
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

    MozillaScaffold(
        modifier = modifier,
        topBar = if (isForOnboarding) null else {
            {
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
                state = scrollState
            ) {
                item {
                    Text(
                        text = state.title,
                        style = MozillaTypography.H3
                    )
                    Spacer(modifier = Modifier.height(MozillaDimension.L))
                }
                item {
                    Text(
                        text = state.subtitle,
                        style = MozillaTypography.H5
                    )
                    Spacer(modifier = Modifier.height(MozillaDimension.M))
                }
                item {
                    Text(
                        text = state.content,
                        style = MozillaTypography.Body2
                    )
                    Spacer(modifier = Modifier.height(MozillaDimension.L))
                }
            }

            if (isForOnboarding) {
                AppPolicyButtons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = MozillaDimension.M,
                            vertical = MozillaDimension.L
                        ),
                    agreeButton = {
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.i_agree),
                            onClick = onAgree,
                            enabled = buttonsEnabled
                        )
                    },
                    disagreeButton = {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.i_disagree),
                            onClick = onDisagree,
                            enabled = buttonsEnabled
                        )
                    }
                )
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