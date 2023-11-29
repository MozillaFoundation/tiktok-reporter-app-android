package org.mozilla.tiktokreporter.studyonboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.TikTokReporterError
import org.mozilla.tiktokreporter.data.model.OnboardingStep
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
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
fun StudyOnboardingScreen(
    viewModel: StudyOnboardingScreenViewModel = hiltViewModel(),
    onNextScreen: () -> Unit
) {
    DialogContainer(
        modifier = Modifier.fillMaxSize()
    ) { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        CollectWithLifecycle(
            flow = viewModel.uiAction,
            onCollect = { action ->
                when (action) {
                    StudyOnboardingScreenViewModel.UiAction.GoToReportForm -> onNextScreen()
                    is StudyOnboardingScreenViewModel.UiAction.ShowError -> {
                        when (action.error) {
                            // internet connection / server unresponsive / server error
                            is TikTokReporterError.NetworkError, is TikTokReporterError.ServerError, is TikTokReporterError.UnknownError -> {
                                dialogState.value = DialogState.ErrorDialog(
                                    title = UiText.StringResource(R.string.error_title_general),
                                    message = UiText.StringResource(R.string.error_message_general),
                                    drawable = R.drawable.error_cat,
                                    actionText = UiText.StringResource(R.string.button_refresh),
                                    action = { } // TODO: refresh
                                )
                            }
                        }
                    }
                }
            }
        )

        if (isLoading) {
            LoadingScreen()
        } else {
            StudyOnboardingScreenContent(
                state = state,
                modifier = Modifier.fillMaxSize(),
                onNextScreen = onNextScreen
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StudyOnboardingScreenContent(
    state: StudyOnboardingScreenViewModel.State,
    modifier: Modifier = Modifier,
    onNextScreen: () -> Unit
) {

    val scope = rememberCoroutineScope()

    MozillaScaffold(
        modifier = modifier
    ) { innerPadding ->

        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { state.steps.size }
        )

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            val stepInfo = state.steps[page]

            OnboardingStepContent(
                onboardingStep = stepInfo,
                modifier = Modifier.fillMaxSize(),
                nextButton = {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.next),
                        onClick = {
                            if (!pagerState.canScrollForward) {
                                onNextScreen()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        page = page + 1
                                    )
                                }
                            }
                        }
                    )
                },
                backButton = {
                    if (pagerState.canScrollBackward) {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.back),
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page = page - 1)
                                }
                            }
                        )
                    }
                },
                skipButton = {
                    if (pagerState.canScrollForward) {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.skip),
                            onClick = onNextScreen
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingStepContent(
    onboardingStep: OnboardingStep,
    modifier: Modifier = Modifier,
    nextButton: (@Composable () -> Unit)? = null,
    backButton: (@Composable () -> Unit)? = null,
    skipButton: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
    ) {

        OnboardingStepInfo(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = MozillaDimension.M,
                top = MozillaDimension.L,
                end = MozillaDimension.M
            ),
            title = onboardingStep.title,
            subtitle = onboardingStep.subtitle,
            description = onboardingStep.description,
            imageUrl = onboardingStep.imageUrl,
            details = onboardingStep.details
        )

        OnboardingStepButtons(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MozillaDimension.M,
                    vertical = MozillaDimension.L
                ),
            nextButton = nextButton,
            backButton = backButton,
            skipButton = skipButton
        )
    }
}

@Composable
private fun OnboardingStepInfo(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    title: String? = null,
    subtitle: String? = null,
    description: String? = null,
    imageUrl: String? = null,
    details: String? = null
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(MozillaDimension.S),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title?.let {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MozillaTypography.H3
                )
            }
        }
        subtitle?.let {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MozillaTypography.H5,
                    color = MozillaColor.Red
                )
            }
        }
        description?.let {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MozillaTypography.Body1
                )
            }
        }
        imageUrl?.let {
            item {
                AsyncImage(
                    modifier = Modifier
                        .fillParentMaxWidth(.6f)
                        .padding(top = MozillaDimension.S),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it)
                        .build(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )
            }
        }
        details?.let {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MozillaTypography.Body2
                )
            }
        }
    }
}

@Composable
private fun OnboardingStepButtons(
    modifier: Modifier = Modifier,
    nextButton: (@Composable () -> Unit)? = null,
    backButton: (@Composable () -> Unit)? = null,
    skipButton: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
    ) {
        nextButton?.let { it() }
        backButton?.let { it() }
        skipButton?.let { it() }
    }
}