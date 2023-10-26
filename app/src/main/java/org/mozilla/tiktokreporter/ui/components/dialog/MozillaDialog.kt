package org.mozilla.tiktokreporter.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import org.mozilla.tiktokreporter.ui.components.PrimaryButton
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun MozillaDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    confirmButtonText: String? = null,
    onConfirm: (() -> Unit)? = null,
    cancelButtonText: String? = null,
    onCancel: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .background(color = MozillaColor.Background)
                .padding(MozillaDimension.L),
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.L)
        ) {
            Text(
                text = title,
                style = MozillaTypography.H5
            )
            Text(
                text = message,
                style = MozillaTypography.Body2
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MozillaDimension.M)
            ) {
                if (cancelButtonText != null && onCancel != null) {
                    SecondaryButton(
                        modifier = Modifier.weight(1f),
                        text = cancelButtonText,
                        onClick = onCancel
                    )
                }
                if (confirmButtonText != null && onConfirm != null) {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        text = confirmButtonText,
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MozillaDialogPreview2Buttons() {
    TikTokReporterTheme {
        MozillaDialog(
            title = "Title",
            message = "Message",
            onDismissRequest = {},
            confirmButtonText = "Confirm",
            onConfirm = { },
            cancelButtonText = "Cancel",
            onCancel = { }
        )
    }
}
@Preview
@Composable
private fun MozillaDialogPreviewConfirmButton() {
    TikTokReporterTheme {
        MozillaDialog(
            title = "Title",
            message = "Message",
            onDismissRequest = {},
            confirmButtonText = "Confirm",
            onConfirm = { }
        )
    }
}
@Preview
@Composable
private fun MozillaDialogPreviewCancelButton() {
    TikTokReporterTheme {
        MozillaDialog(
            title = "Title",
            message = "Message",
            onDismissRequest = {},
            cancelButtonText = "Cancel",
            onCancel = { }
        )
    }
}