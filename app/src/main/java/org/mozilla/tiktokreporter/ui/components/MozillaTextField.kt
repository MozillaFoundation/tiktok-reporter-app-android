package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun mozillaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MozillaColor.TextColor,
    unfocusedTextColor = MozillaColor.TextColor,
    disabledTextColor = MozillaColor.TextColorDisabled,
    errorTextColor = MozillaColor.Error,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    cursorColor = Color.Black,
    errorCursorColor = MozillaColor.Error,
    selectionColors = TextSelectionColors(
        handleColor = Color.Black,
        backgroundColor = Color.Black
    ),
    focusedBorderColor = MozillaColor.Blue,
    unfocusedBorderColor = Color.Black,
    disabledBorderColor = Color.Black.copy(alpha = .3f),
    errorBorderColor = MozillaColor.Error,
    focusedLeadingIconColor = MozillaColor.Blue,
    unfocusedLeadingIconColor = Color.Black,
    disabledLeadingIconColor = Color.Black.copy(alpha = .3f),
    errorLeadingIconColor = MozillaColor.Error,
    focusedTrailingIconColor = MozillaColor.Blue,
    unfocusedTrailingIconColor = Color.Black,
    disabledTrailingIconColor = Color.Black.copy(alpha = .3f),
    errorTrailingIconColor = MozillaColor.Error,
    focusedLabelColor = MozillaColor.TextColor,
    unfocusedLabelColor = MozillaColor.TextColorDisabled,
    disabledLabelColor = MozillaColor.TextColorDisabled,
    errorLabelColor = MozillaColor.Error,
    focusedPlaceholderColor = MozillaColor.TextColorDisabled,
    unfocusedPlaceholderColor = MozillaColor.TextColorDisabled,
    disabledPlaceholderColor = MozillaColor.TextColorDisabled,
    errorPlaceholderColor = MozillaColor.Error,
    focusedSupportingTextColor = MozillaColor.TextColor,
    unfocusedSupportingTextColor = MozillaColor.TextColor,
    disabledSupportingTextColor = MozillaColor.TextColorDisabled,
    errorSupportingTextColor = MozillaColor.Error,
    focusedPrefixColor = MozillaColor.TextColor,
    unfocusedPrefixColor = MozillaColor.TextColor,
    disabledPrefixColor = MozillaColor.TextColorDisabled,
    errorPrefixColor = MozillaColor.Error,
    focusedSuffixColor = MozillaColor.TextColor,
    unfocusedSuffixColor = MozillaColor.TextColor,
    disabledSuffixColor = MozillaColor.TextColorDisabled,
    errorSuffixColor = MozillaColor.Error,
)
@Composable
fun MozillaTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = false,
    readOnly: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: TextFieldColors = mozillaTextFieldColors(),
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = modifier,
        enabled = enabled,
        label = {
            if (!label.isNullOrBlank()) {
                Text(text = label)
            }
        },
        placeholder = {
            if (!placeholder.isNullOrBlank()) {
                Text(text = placeholder)
            }
        },
        maxLines = maxLines,
        singleLine = singleLine,
        textStyle = MozillaTypography.Body1,
        colors = colors,
        readOnly = readOnly,
        trailingIcon = trailingIcon
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL
)
@Composable
private fun MozillaTextFieldPreview() {
    TikTokReporterTheme {
        Column(Modifier.fillMaxSize()) {
            MozillaTextField(
                text = "",
                onTextChanged = { },
                label = "TikTok Link"
            )
            MozillaTextField(
                text = "",
                onTextChanged = { },
                label = "TikTok Link",
                enabled = false
            )
            MozillaTextField(
                text = "ala bala",
                onTextChanged = { },
                label = "TikTok Link"
            )
            MozillaTextField(
                text = "ala bala",
                onTextChanged = { },
                label = "TikTok Link",
                enabled = false
            )
            MozillaTextField(
                text = "",
                placeholder = "ala bala",
                onTextChanged = { },
                label = "TikTok Link"
            )
            MozillaTextField(
                text = "",
                placeholder = "ala bala",
                onTextChanged = { },
                label = "TikTok Link",
                enabled = false
            )
        }
    }
}