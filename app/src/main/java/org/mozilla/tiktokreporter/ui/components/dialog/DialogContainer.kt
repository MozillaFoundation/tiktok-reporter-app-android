package org.mozilla.tiktokreporter.ui.components.dialog

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun DialogContainer(
    modifier: Modifier = Modifier,
    content: @Composable (MutableState<DialogState>) -> Unit
) {
    val dialogState = remember { mutableStateOf<DialogState>(DialogState.Nothing) }

    when (val dialogData = dialogState.value) {
        is DialogState.Custom -> {
            dialogData.dialog(dialogState)
        }
        is DialogState.Message -> {
            MozillaDialog(
                onDismissRequest = {
                    dialogData.onDismissRequest()
                },
                title = dialogData.title,
                message = dialogData.message,
                confirmButtonText = dialogData.positiveButtonText,
                onConfirm = dialogData.onPositive,
                cancelButtonText = dialogData.negativeButtonText,
                onCancel = dialogData.onNegative
            )
        }

        else  -> Unit
    }

    Surface(
        modifier = modifier,
        content = { content(dialogState) }
    )
}

sealed class DialogState {
    data object Nothing : DialogState()

    data class Message(
        val title: String,
        val message: String = "",
        val positiveButtonText: String? = null,
        val onPositive: (() -> Unit)? = null,
        val negativeButtonText: String? = null,
        val onNegative: (() -> Unit)? = null,
        val onDismissRequest: () -> Unit = { },
    ) : DialogState()

    data class Custom(
        val dialog: @Composable (MutableState<DialogState>) -> Unit
    ): DialogState()
}