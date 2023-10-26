package org.mozilla.tiktokreporter.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MozillaTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "TikTok Reporter",
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
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MozillaTypography.H5
            )
        },
        modifier = modifier,
        navigationIcon = navItem,
        actions = {
            action()
        },
        colors = colors
    )
}