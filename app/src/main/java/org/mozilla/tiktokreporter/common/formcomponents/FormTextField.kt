package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.ui.components.MozillaTextField
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun FormTextField(
    field: FormFieldUiComponent.TextField,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (field.readOnly) {
        Text(
            text = field.value,
            style = MozillaTypography.Body1
        )
    } else {
        Column(
            modifier = modifier
        ) {
            if (field.description.isNotBlank()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = field.description,
                    style = MozillaTypography.Body2
                )
                Spacer(modifier = Modifier.height(MozillaDimension.S))
            }

            MozillaTextField(
                modifier = Modifier.fillMaxWidth(),
                text = field.value,
                onTextChanged = onTextChanged,
                label = field.label,
                placeholder = field.placeholder,
                maxLines = field.maxLines,
                multiline = field.multiline,
                readOnly = field.readOnly
            )
        }
    }
}