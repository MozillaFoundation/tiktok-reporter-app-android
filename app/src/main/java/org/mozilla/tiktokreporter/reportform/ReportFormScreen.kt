package org.mozilla.tiktokreporter.reportform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.data.model.FormField
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaDropdown
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaSlider
import org.mozilla.tiktokreporter.ui.components.MozillaTextField
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun ReportFormScreen(
    viewModel: ReportFormScreenViewModel = hiltViewModel(),
    onGoToSettings: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
    onGoToAppPurpose: () -> Unit
) {
    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val action = state.action?.get()
        val isLoading = action is ReportFormScreenViewModel.UiAction.ShowLoading

        if (isLoading) {
            LoadingScreen()
        } else {
            ReportFormScreenContent(
                state = state,
                onGoToSettings = onGoToSettings,
                modifier = Modifier.fillMaxSize()
            )
        }

        when (action) {
            else -> Unit
        }
    }
}

@Composable
private fun ReportFormScreenContent(
    state: ReportFormScreenViewModel.State,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    MozillaScaffold(
        modifier = modifier,
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                action = {
                    IconButton(onClick = onGoToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "",
                            tint = MozillaColor.TextColor
                        )
                    }
                }
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
            ),
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.L),
            content = {
                items(state.formFields) { field ->
                    when (field) {
                        is FormField.TextField -> {
                            FormTextField(
                                field = field,
                                onTextChanged = { },
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }
                        is FormField.DropDown -> {
                            FormDropDown(
                                field = field,
                                onOptionChanged = { },
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }
                        is FormField.Slider -> {
                            FormSlider(
                                field = field,
                                onValueChanged = { },
                                modifier = Modifier.fillParentMaxWidth()
                            )
                        }

                        else -> Unit
                    }
                }
            }
        )
    }
}

@Composable
private fun FormTextField(
    field: FormField.TextField,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            onTextChanged = {
                text = it
                onTextChanged(it)
            },
            label = field.label,
            placeholder = field.placeholder,
            maxLines = field.maxLines,
            multiline = field.multiline,
        )
    }
}

@Composable
private fun FormDropDown(
    field: FormField.DropDown,
    onOptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember {
        mutableIntStateOf(field.options.indexOfFirst { it.title == field.selected })
    }
    Column(
        modifier = modifier
    ) {
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaDropdown(
            modifier = Modifier.fillMaxWidth(),
            options = field.options.map { it.title },
            onOptionSelected = { index, option ->
                selectedIndex = index
                onOptionChanged(option)
            },
            selectedIndex = selectedIndex,
            label = field.label,
            placeholder = field.placeholder,
        )
    }
}

@Composable
private fun FormSlider(
    field: FormField.Slider,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
    ) {
        if (field.label.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.label,
                style = MozillaTypography.Body1
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaSlider(
            modifier = Modifier.fillMaxWidth(),
            sliderPosition = sliderPosition,
            max = field.max,
            step = field.step,
            onValueChanged = onValueChanged,
            leftLabel = field.leftLabel,
            rightLabel = field.rightLabel
        )
    }
}