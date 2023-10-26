package org.mozilla.tiktokreporter.onboarding.termsconditions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.components.dialog.DialogState

@Composable
fun TermsAndConditionsScreen(
    viewModel: TermsAndConditionsViewModel = hiltViewModel(),
    onNextScreen: () -> Unit = { }
) {
    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsState()

        TermsAndConditionsScreenContent(
            state = state,
            onAgree = onNextScreen,
            onDisagree = {
                dialogState.value = DialogState.Message(
                    title = "Review the terms & conditions",
                    message = "Please read these terms and consitions carefully before using TikTok Reporter",
                    positiveButtonText = "Got it",
                    onPositive = {
                        dialogState.value = DialogState.Nothing
                    },
                    onDismissRequest = {
                        dialogState.value = DialogState.Nothing
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TermsAndConditionsScreenContent(
    state: TermsAndConditionsViewModel.State,
    onAgree: () -> Unit,
    onDisagree: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(text = state.title)
            Text(text = state.subtitle)
            Text(text = state.content)
        }
    }
}