package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.common.FormFieldError
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.ui.components.MozillaTextFieldWithLengthLimit
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
            text = field.value, style = MozillaTypography.Body1
        )
    } else {
        Column(
            modifier = modifier
        ) {
            if (field.description.isNotBlank()) {
                Text(
                    modifier = Modifier.fillMaxWidth(), text = field.description, style = MozillaTypography.Body2
                )
                Spacer(modifier = Modifier.height(MozillaDimension.S))
            }

            MozillaTextFieldWithLengthLimit(modifier = Modifier.fillMaxWidth(),
                text = field.value,
                onTextChanged = onTextChanged,
                label = field.label,
                placeholder = field.placeholder,
                maxLines = field.maxLines,
                multiline = field.multiline,
                readOnly = field.readOnly,
                errorText = field.error?.let {
                    when (it) {
                        FormFieldError.Empty -> stringResource(id = R.string.error_message_empty_field)
                        FormFieldError.EmptyCategory -> stringResource(id = R.string.error_message_empty_category)
                        FormFieldError.NoTikTokLink -> stringResource(id = R.string.error_message_tik_tok_link)
                    }
                })
        }
    }
}