package org.mozilla.tiktokreporter.reportformcompleted

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@OptIn(ExperimentalTextApi::class)
@Composable
fun ReportFormCompletedScreen(
    onNavigateBack: () -> Unit,
    onGoToSettings: () -> Unit
) {
    MozillaScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                action = {
                    IconButton(
                        onClick = onGoToSettings
                    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    vertical = MozillaDimension.L,
                    horizontal = MozillaDimension.M
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier.size(120.dp),
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "",
                    tint = MozillaColor.Success
                )
                Text(
                    text = stringResource(id = R.string.report_submitted),
                    style = MozillaTypography.Success,
                    color = MozillaColor.Success,
                    textAlign = TextAlign.Center
                )
            }

            SecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_im_done),
                onClick = onNavigateBack
            )
        }
    }
}