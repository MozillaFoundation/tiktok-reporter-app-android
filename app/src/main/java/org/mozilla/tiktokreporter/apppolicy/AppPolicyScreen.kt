package org.mozilla.tiktokreporter.apppolicy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onNextScreen: () -> Unit = { },
    onNavigateBack: () -> Unit = { },
) {
    DialogContainer(
        modifier = Modifier.fillMaxSize()
    ) { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val action = state.action?.get()
        val isLoading = action is AppPolicyScreenViewModel.UiAction.ShowLoading

        if (isLoading) {
            LoadingScreen()
        } else {
            AppPolicyScreenContent(
                state = state,
                isForOnboarding = isForOnboarding,
                onNavigateBack = onNavigateBack,
                onAgree = {
                    if (isForOnboarding) {
                        viewModel.acceptTerms()
                        onNextScreen()
                    }
                },
                onDisagree = if (isForOnboarding) {
                    {
                        dialogState.value = DialogState.Message(
                            title = "Review the terms & conditions",
                            message = "Please read these terms and conditions carefully before using TikTok Reporter",
                            negativeButtonText = "Got it",
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
    MozillaScaffold(
        modifier = modifier,
        topBar = if (isForOnboarding) null else {
            {
                MozillaTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    action = {
                        IconButton(
                            onClick = onNavigateBack
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
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
                )
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
                            text = "I agree",
                            onClick = onAgree
                        )
                    },
                    disagreeButton = {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = "I disagree",
                            onClick = onDisagree
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