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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onGoToAppPurpose: () -> Unit,
    onGoToStudies: () -> Unit,
    onGoToEmail: () -> Unit,
    onGoToTermsAndConditions: () -> Unit,
    onGoToPrivacyPolicy: () -> Unit,
    onGoToDataHandling: () -> Unit,
) {
    SettingsScreenContent(
        modifier = Modifier.fillMaxSize(),
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
                item {
                    SettingEntry(
                        modifier = Modifier.fillParentMaxWidth(),
                        title = "ABOUT TIKTOK REPORTER",
                        onClick = onGoToAppPurpose
                    )
                }
                item {
                    SettingEntry(
                        modifier = Modifier.fillParentMaxWidth(),
                        title = "STUDIES",
                        onClick = onGoToStudies
                    )
                }
                item {
                    SettingEntry(
                        modifier = Modifier.fillParentMaxWidth(),
                        title = "EMAIL ADDRESS",
                        onClick = onGoToEmail
                    )
                }
                item {
                    SettingEntry(
                        modifier = Modifier.fillParentMaxWidth(),
                        title = "TERMS & CONDITIONS",
                        onClick = onGoToTermsAndConditions
                    )
                }
                item {
                    SettingEntry(
                        modifier = Modifier.fillParentMaxWidth(),
                        title = "PRIVACY POLICY",
                        onClick = onGoToPrivacyPolicy
                    )
                }
                item {
                    SettingEntry(
                        modifier = Modifier.fillParentMaxWidth(),
                        title = "DATA HANDLING",
                        onClick = onGoToDataHandling
                    )
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