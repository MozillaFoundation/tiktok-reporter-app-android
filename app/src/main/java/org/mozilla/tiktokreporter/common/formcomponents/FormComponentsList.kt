package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.common.FormFieldUiComponent

@Composable
fun formComponentsItems(
    formFields: List<FormFieldUiComponent<*>>, onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit
) {
    formFields.filter { it.isVisible }.forEach { field ->
        when (field) {
            is FormFieldUiComponent.TextField -> {
                FormTextField(modifier = Modifier.fillMaxWidth(), field = field, onTextChanged = {
                    onFormFieldValueChanged(field.id, it)
                })
            }

            is FormFieldUiComponent.DropDown -> {
                FormDropDown(modifier = Modifier.fillMaxWidth(), field = field, onOptionChanged = {
                    onFormFieldValueChanged(field.id, it)
                })
            }

            is FormFieldUiComponent.Slider -> {
                FormSlider(modifier = Modifier.fillMaxWidth(), field = field, onValueChanged = {
                    onFormFieldValueChanged(field.id, it)
                })
            }
        }
    }
}