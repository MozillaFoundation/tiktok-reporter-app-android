package org.mozilla.tiktokreporter.termsconditions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.PrimaryButton
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.components.dialog.DialogState
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.util.emptyCallback

@Composable
fun TermsAndConditionsScreen(
    viewModel: TermsAndConditionsScreenViewModel = hiltViewModel(),
    isForOnboarding: Boolean = true,
    onNextScreen: () -> Unit = { }
) {
    DialogContainer(
        modifier = Modifier.fillMaxSize()
    ) { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val action = state.action?.get()
        val isLoading = action is TermsAndConditionsScreenViewModel.UiAction.ShowLoading

        if (isLoading) {
            LoadingScreen()
        } else {
            TermsAndConditionsScreenContent(
                state = state,
                isForOnboarding = isForOnboarding,
                onAgree = if (isForOnboarding) onNextScreen else emptyCallback,
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
private fun TermsAndConditionsScreenContent(
    state: TermsAndConditionsScreenViewModel.State,
    isForOnboarding: Boolean,
    onAgree: () -> Unit,
    onDisagree: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MozillaScaffold(
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
            if (isForOnboarding) {
                item {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "I agree",
                        onClick = onAgree
                    )
                    Spacer(modifier = Modifier.height(MozillaDimension.S))
                }
                item {
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "I disagree",
                        onClick = {
                            onDisagree()
                        }
                    )
                }
            }
        }
    }
}