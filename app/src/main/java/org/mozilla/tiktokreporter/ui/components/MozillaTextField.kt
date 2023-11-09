package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun mozillaTextFieldColors(
    isFilled: Boolean
) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MozillaColor.TextColor,
    unfocusedTextColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledTextColor = MozillaColor.Disabled,
    errorTextColor = MozillaColor.TextColor,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    cursorColor = MozillaColor.TextColor,
    errorCursorColor = MozillaColor.TextColor,
    selectionColors = TextSelectionColors(
        handleColor = MozillaColor.TextColor,
        backgroundColor = MozillaColor.Divider
    ),
    focusedBorderColor = MozillaColor.Blue,
    unfocusedBorderColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledBorderColor = MozillaColor.Disabled,
    errorBorderColor = MozillaColor.Error,
    focusedLeadingIconColor = MozillaColor.Blue,
    unfocusedLeadingIconColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledLeadingIconColor = MozillaColor.Disabled,
    errorLeadingIconColor = MozillaColor.TextColor,
    focusedTrailingIconColor = MozillaColor.Blue,
    unfocusedTrailingIconColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledTrailingIconColor = MozillaColor.Disabled,
    errorTrailingIconColor = MozillaColor.TextColor,
    focusedLabelColor = MozillaColor.TextColor,
    unfocusedLabelColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledLabelColor = MozillaColor.Disabled,
    errorLabelColor = MozillaColor.Inactive,
    focusedPlaceholderColor = MozillaColor.Inactive,
    unfocusedPlaceholderColor = MozillaColor.Inactive,
    disabledPlaceholderColor = MozillaColor.Disabled,
    errorPlaceholderColor = MozillaColor.Inactive,
    focusedSupportingTextColor = MozillaColor.TextColor,
    unfocusedSupportingTextColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledSupportingTextColor = MozillaColor.Disabled,
    errorSupportingTextColor = MozillaColor.Error,
    focusedPrefixColor = MozillaColor.TextColor,
    unfocusedPrefixColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledPrefixColor = MozillaColor.Disabled,
    errorPrefixColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    focusedSuffixColor = MozillaColor.TextColor,
    unfocusedSuffixColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
    disabledSuffixColor = MozillaColor.Disabled,
    errorSuffixColor = if (isFilled) MozillaColor.TextColor else MozillaColor.Inactive,
)
@Composable
fun MozillaTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    multiline: Boolean = false,
    readOnly: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: TextFieldColors = mozillaTextFieldColors(text.isNotBlank()),
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
        minLines = if (multiline) maxLines else 1,
        singleLine = !multiline,
        textStyle = MozillaTypography.Body1,
        colors = colors,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        shape = RectangleShape,
        isError = errorText != null,
        supportingText = errorText?.let {
            {
                Text(
                    text = it,
                    style = MozillaTypography.Body2
                )
            }
        }
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun MozillaTextFieldPreviewEnabled() {
    TikTokReporterTheme {
        Column(Modifier.fillMaxWidth()) {
            MozillaTextField(
                text = "",
                label = "label",
                placeholder = "",
                onTextChanged = { },
                enabled = true
            )
            MozillaTextField(
                text = "text",
                label = "label",
                placeholder = "",
                onTextChanged = { },
                enabled = true
            )
            MozillaTextField(
                text = "",
                label = "label",
                placeholder = "placeholder",
                onTextChanged = { },
                enabled = true
            )
            MozillaTextField(
                text = "",
                label = "",
                placeholder = "placeholder",
                onTextChanged = { },
                enabled = true
            )
            MozillaTextField(
                text = "",
                label = "label",
                placeholder = "placeholder",
                onTextChanged = { },
                enabled = true,
                errorText = "This field cannot be empty!"
            )
        }
    }
}
@Preview(
    showBackground = true
)
@Composable
private fun MozillaTextFieldPreviewDisabled() {
    TikTokReporterTheme {
        Column(Modifier.fillMaxWidth()) {
            MozillaTextField(
                text = "",
                label = "label",
                onTextChanged = { },
                enabled = false
            )
            MozillaTextField(
                text = "text",
                label = "label",
                onTextChanged = { },
                enabled = false
            )
            MozillaTextField(
                text = "",
                label = "label",
                placeholder = "placeholder",
                onTextChanged = { },
                enabled = false
            )

            MozillaTextField(
                text = "",
                label = "",
                placeholder = "placeholder",
                onTextChanged = { },
                enabled = false
            )
        }
    }
}