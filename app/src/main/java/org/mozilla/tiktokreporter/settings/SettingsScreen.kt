package org.mozilla.tiktokreporter.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onGoToAppPurpose: () -> Unit,
    onGoToStudies: () -> Unit,
    onGoToEmail: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
    onGoToDataHandling: () -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = Modifier.fillMaxSize(),
        entries = state.entries,
        onNavigateBack = onNavigateBack,
        onGoToAppPurpose = onGoToAppPurpose,
        onGoToStudies = onGoToStudies,
        onGoToEmail = onGoToEmail,
        onGoToTermsAndConditions = onGoToTermsAndConditions,
        onGoToPrivacyPolicy = onGoToPrivacyPolicy,
        onGoToDataHandling = onGoToDataHandling
    )
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    entries: List<SettingsScreenViewModel.SettingsEntry>,
    onNavigateBack: () -> Unit,
    onGoToAppPurpose: () -> Unit,
    onGoToStudies: () -> Unit,
    onGoToEmail: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
    onGoToDataHandling: () -> Unit,
) {
    MozillaScaffold(
        modifier = modifier,
        topBar = {
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
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = MozillaDimension.M,
                vertical = MozillaDimension.L
            ),
            content = {
                item {
                    Text(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(bottom = MozillaDimension.M),
                        text = "Settings",
                        style = MozillaTypography.H3
                    )
                }

                items(entries) { settingsEntry ->
                    when (settingsEntry) {
                        SettingsScreenViewModel.SettingsEntry.About -> {
                            SettingEntry(
                                modifier = Modifier.fillParentMaxWidth(),
                                title = stringResource(id = R.string.about_tik_tok_reporter).uppercase(),
                                onClick = onGoToAppPurpose
                            )
                        }
                        SettingsScreenViewModel.SettingsEntry.Studies -> {
                            SettingEntry(
                                modifier = Modifier.fillParentMaxWidth(),
                                title = stringResource(id = R.string.studies).uppercase(),
                                onClick = onGoToStudies
                            )
                        }
                        SettingsScreenViewModel.SettingsEntry.Email -> {
                            SettingEntry(
                                modifier = Modifier.fillParentMaxWidth(),
                                title = stringResource(id = R.string.email_address).uppercase(),
                                onClick = onGoToEmail
                            )
                        }
                        SettingsScreenViewModel.SettingsEntry.Terms -> {
                            SettingEntry(
                                modifier = Modifier.fillParentMaxWidth(),
                                title = stringResource(id = R.string.terms_and_conditions).uppercase(),
                                onClick = onGoToTermsAndConditions
                            )
                        }
                        SettingsScreenViewModel.SettingsEntry.Privacy -> {
                            SettingEntry(
                                modifier = Modifier.fillParentMaxWidth(),
                                title = stringResource(id = R.string.privacy_policy).uppercase(),
                                onClick = onGoToPrivacyPolicy
                            )
                        }
                        SettingsScreenViewModel.SettingsEntry.DataHandling -> {
                            SettingEntry(
                                modifier = Modifier.fillParentMaxWidth(),
                                title = stringResource(id = R.string.data_handling).uppercase(),
                                onClick = onGoToDataHandling
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun SettingEntry(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MozillaDimension.L),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MozillaTypography.Body2,
                color = MozillaColor.TextColor
            )

            Icon(
                modifier = Modifier.size(MozillaDimension.L),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                tint = MozillaColor.TextColor
            )
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = MozillaColor.Divider
        )
    }
}