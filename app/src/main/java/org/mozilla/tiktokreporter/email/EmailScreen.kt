package org.mozilla.tiktokreporter.email

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.mozilla.tiktokreporter.common.formcomponents.formComponentsItems
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.PrimaryButton
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension

@Composable
fun EmailScreen(
    viewModel: EmailScreenViewModel = hiltViewModel(),
    isForOnboarding: Boolean = true,
    onNextScreen: () -> Unit,
    onNavigateBack: () -> Unit
) {
    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        when (state.action?.get()) {
            EmailScreenViewModel.UiAction.GoToReportForm -> onNextScreen()
            EmailScreenViewModel.UiAction.EmailSaved -> {
                if (isForOnboarding) onNextScreen()
                else onNavigateBack()
            }
            else -> Unit
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            EmailScreenContent(
                state = state,
                onFormFieldValueChanged = viewModel::onFormFieldValueChanged,
                onNavigateBack = onNavigateBack,
                onSaveEmail = viewModel::onSaveEmail,
                onNextScreen = onNextScreen,
                isForOnboarding = isForOnboarding,
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
    isForOnboarding: Boolean,
    modifier: Modifier = Modifier
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
                ),
                verticalArrangement = Arrangement.spacedBy(MozillaDimension.L),
                content = {
                    formComponentsItems(
                        formFields = state.formFields,
                        onFormFieldValueChanged = onFormFieldValueChanged
                    )
                }
            )

            OnboardingFormButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MozillaDimension.M,
                        vertical = MozillaDimension.L
                    ),
                nextButton = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Next",
                        onClick = onSaveEmail
                    )
                },
                skipButton = if (isForOnboarding) {
                    {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Skip",
                            onClick = onNextScreen
                        )
                    }
                } else null
            )
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