package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.data.model.FormField
import org.mozilla.tiktokreporter.ui.components.MozillaDropdown
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun FormDropDown(
    field: FormField.DropDown,
    value: String,
    onOptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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

        MozillaDropdown(
            modifier = Modifier.fillMaxWidth(),
            options = field.options.map { it.title },
            onOptionSelected = { index, option ->
                onOptionChanged(option)
            },
            text = value,
            label = field.label,
            placeholder = field.placeholder,
        )
    }
}