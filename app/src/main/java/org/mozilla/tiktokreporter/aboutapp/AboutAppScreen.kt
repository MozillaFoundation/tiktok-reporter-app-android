package org.mozilla.tiktokreporter.aboutapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography


@Composable
fun AboutAppScreen(
    onNavigateBack: () -> Unit
) {

    MozillaScaffold(
        modifier = Modifier.fillMaxSize(),
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
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.L),
            content = {
                item {
                    Text(
                        text = "${stringResource(id = R.string.about)} ${stringResource(id = R.string.app_name)}",
                        style = MozillaTypography.H3
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.about_app_content),
                        style = MozillaTypography.Body2
                    )
                }
            }
        )
    }
}