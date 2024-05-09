package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MozillaTopAppBar(
    modifier: Modifier = Modifier,
    navItem: @Composable () -> Unit = { },
    action: @Composable () -> Unit = { }
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MozillaColor.Background,
        scrolledContainerColor = MozillaColor.Background,
        navigationIconContentColor = MozillaColor.TextColor,
        titleContentColor = MozillaColor.TextColor,
        actionIconContentColor = MozillaColor.TextColor
    )
    Box(
        modifier = modifier
    ) {
        TopAppBar(
            title = {
                Text(text="")
            },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = navItem,
            actions = {
                action()
            },
            colors = colors
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun TopAppBarPreview() {
    TikTokReporterTheme {
        MozillaTopAppBar()
    }
}