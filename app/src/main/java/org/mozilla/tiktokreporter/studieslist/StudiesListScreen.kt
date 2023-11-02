package org.mozilla.tiktokreporter.studieslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.data.model.StudyOverview
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaRadioButton
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.components.dialog.DialogState
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun StudiesListScreen(
    viewModel: StudiesListScreenViewModel = hiltViewModel(),
    onNextScreen: () -> Unit
) {
    DialogContainer(
        modifier = Modifier.fillMaxSize()
    ) { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val action = state.action?.get()
        val isLoading = action is StudiesListScreenViewModel.UiAction.ShowLoading

        when (action) {
            StudiesListScreenViewModel.UiAction.OnNextScreen -> onNextScreen()
            StudiesListScreenViewModel.UiAction.OnNoStudySelected -> {
                dialogState.value = DialogState.Message(
                    title = "No study selected",
                    message = "Please choose a study to participate in.",
                    positiveButtonText = "Got it",
                    onPositive = { dialogState.value = DialogState.Nothing },
                    onDismissRequest = { dialogState.value = DialogState.Nothing }
                )
            }

            else -> Unit
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            StudiesListScreenContent(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onStudySelected = viewModel::selectStudyAtIndex,
                onSave = viewModel::onSave
            )
        }
    }
}

@Composable
private fun StudiesListScreenContent(
    modifier: Modifier = Modifier,
    state: StudiesListScreenViewModel.State,
    onStudySelected: (Int) -> Unit,
    onSave: () -> Unit
) {
    MozillaScaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MozillaDimension.M)
                .padding(bottom = MozillaDimension.L),
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.L)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MozillaDimension.L),
                contentPadding = PaddingValues(top = MozillaDimension.L)
            ) {
                item {
                    Text(
                        text = "Select a study to participate in",
                        style = MozillaTypography.H3
                    )
                }
                item {
                    Text(
                        text = "We may choose to run a few different studies simultaneously. These are the studies available to you based on the information you provided.",
                        style = MozillaTypography.Body2
                    )
                }
                itemsIndexed(state.studies) { index, study ->
                    StudyEntry(
                        studyOverview = study,
                        onClick = { onStudySelected(index) },
                        modifier = Modifier.fillParentMaxWidth()
                    )
                }
            }

            SecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Next",
                onClick = onSave
            )
        }
    }
}

@Composable
private fun studyEntryConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val radioButton = createRefFor("radioButton")
        val title = createRefFor("title")
        val description = createRefFor("description")

        constrain(radioButton) {
            start.linkTo(parent.start)
            top.linkTo(title.top)
            end.linkTo(title.start, margin = MozillaDimension.M)
            bottom.linkTo(title.bottom)
        }

        constrain(title) {
            start.linkTo(radioButton.end)
            top.linkTo(parent.top)
            end.linkTo(parent.end)

            width = Dimension.fillToConstraints
        }

        constrain(description) {
            start.linkTo(title.start)
            top.linkTo(radioButton.bottom, margin = MozillaDimension.XS)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)

            width = Dimension.fillToConstraints
        }
    }
}

@Composable
private fun StudyEntry(
    studyOverview: StudyOverview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .wrapContentHeight(),
        constraintSet = studyEntryConstraintSet()
    ) {
        MozillaRadioButton(
            modifier = Modifier.layoutId("radioButton"),
            selected = studyOverview.isSelected,
            onClick = onClick
        )
        Text(
            modifier = Modifier.layoutId("title"),
            text = studyOverview.name,
            style = MozillaTypography.Body1
        )
        Text(
            modifier = Modifier.layoutId("description"),
            text = studyOverview.description,
            style = MozillaTypography.Body2
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun StudyEntryPreview() {
    TikTokReporterTheme {
        StudyEntry(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            studyOverview = StudyOverview(
                id = "1",
                name = "Study name",
                description = "Study description",
                isActive = true,
                isSelected = false
            ),
            onClick = {}
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun SelectedStudyEntryPreview() {
    TikTokReporterTheme {
        StudyEntry(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            studyOverview = StudyOverview(
                id = "1",
                name = "Study name",
                description = "Study description",
                isActive = true,
                isSelected = true
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun StudiesListScreenPreview() {
    TikTokReporterTheme {
        StudiesListScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = StudiesListScreenViewModel.State(
                studies = (1..20).map {
                    StudyOverview(
                        id = it.toString(),
                        name = "Study name",
                        description = "Study description",
                        isActive = true,
                        isSelected = true
                    )
                }
            ),
            onStudySelected = { },
            onSave = { }
        )
    }
}