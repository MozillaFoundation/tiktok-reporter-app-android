package org.mozilla.tiktokreporter.ui.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MozillaDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: (@Composable () -> Unit)? = null,
    cancelButton: (@Composable () -> Unit)? = null,
) {

}