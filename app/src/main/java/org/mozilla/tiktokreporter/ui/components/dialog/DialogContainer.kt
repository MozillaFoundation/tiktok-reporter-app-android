package org.mozilla.tiktokreporter.ui.components.dialog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.common.ErrorContent
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.util.UiText

@Composable
fun DialogContainer(
    modifier: Modifier = Modifier,
    content: @Composable (MutableState<DialogState>) -> Unit
) {
    val dialogState = remember { mutableStateOf<DialogState>(DialogState.Nothing) }

    Surface(
        modifier = modifier,
        color = MozillaColor.Background,
        content = { content(dialogState) }
    )

    when (val dialogData = dialogState.value) {
        is DialogState.Custom -> {
            dialogData.dialog(dialogState)
        }
        is DialogState.MessageDialog -> {
            MozillaDialog(
                onDismissRequest = {
                    dialogData.onDismissRequest()
                },
                title = dialogData.title.asString(),
                message = dialogData.message.asString(),
                confirmButtonText = dialogData.positiveButtonText?.asString(),
                onConfirm = dialogData.onPositive,
                cancelButtonText = dialogData.negativeButtonText?.asString(),
                onCancel = dialogData.onNegative
            )
        }

        is DialogState.ErrorDialog -> {
            ErrorContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MozillaColor.Background)
                    .systemBarsPadding()
                    .padding(MozillaDimension.L),
                drawable = dialogData.drawable,
                title = dialogData.title.asString(),
                message = dialogData.message.asString(),
                action = if (dialogData.actionText != null && dialogData.action != null) {
                    {
                        SecondaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = dialogData.actionText.asString(),
                            onClick = dialogData.action
                        )
                    }
                } else null
            )
        }

        else  -> Unit
    }
}

sealed class DialogState {
    data object Nothing : DialogState()

    data class MessageDialog(
        val title: UiText,
        val message: UiText = UiText.DynamicString(""),
        val positiveButtonText: UiText? = null,
        val onPositive: (() -> Unit)? = null,
        val negativeButtonText: UiText? = null,
        val onNegative: (() -> Unit)? = null,
        val onDismissRequest: () -> Unit = { },
    ) : DialogState()

    data class ErrorDialog(
        val title: UiText,
        val message: UiText,
        @DrawableRes val drawable: Int? = null,
        val actionText: UiText? = null,
        val action: (() -> Unit)? = null
    ) : DialogState()

    data class Custom(
        val dialog: @Composable (MutableState<DialogState>) -> Unit
    ) : DialogState()
}