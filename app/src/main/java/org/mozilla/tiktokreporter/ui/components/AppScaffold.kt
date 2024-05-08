package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.ui.theme.MozillaColor

@Composable
fun MozillaScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (topBar != null) {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            containerColor = MozillaColor.Background,
            contentColor = MozillaColor.TextColor,
            content = content
        )
    } else {
        Scaffold(
            modifier = modifier,
            containerColor = MozillaColor.Background,
            contentColor = MozillaColor.TextColor,
            content = content
        )
    }

}