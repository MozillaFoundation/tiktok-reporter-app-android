package org.mozilla.tiktokreporter.onboarding.termsconditions

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.PrimaryButton
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.components.dialog.DialogState
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun TermsAndConditionsScreen(
    viewModel: TermsAndConditionsViewModel = hiltViewModel(),
    onNextScreen: () -> Unit = { }
) {
    DialogContainer(
        modifier = Modifier.fillMaxSize()
    ) { dialogState ->

        val state by viewModel.state.collectAsState()

        TermsAndConditionsScreenContent(
            state = state,
            onAgree = onNextScreen,
            onDisagree = {
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
        containerColor = MozillaColor.Background,
        contentColor = MozillaColor.TextColor,
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth()
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