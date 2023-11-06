package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.common.FormTextField
import org.mozilla.tiktokreporter.data.model.FormField

fun LazyListScope.formComponentsItems(
    formFields: List<FormFieldUiComponent>,
    onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit
) {
    items(formFields) { field ->
        when (val formField = field.formField) {
            is FormField.TextField -> {
                FormTextField(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = formField,
                    value = field.value as String,
                    onTextChanged = {
                        onFormFieldValueChanged(formField.id, it)
                    },
                    readOnly = field.readOnly
                )
            }

            is FormField.DropDown -> {
                FormDropDown(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = formField,
                    value = field.value as String,
                    onOptionChanged = {
                        onFormFieldValueChanged(formField.id, it)
                    }
                )
            }

            is FormField.Slider -> {
                FormSlider(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = formField,
                    sliderPosition = field.value as Int,
                    onValueChanged = {
                        onFormFieldValueChanged(formField.id, it)
                    }
                )
            }

            else -> Unit
        }
    }
}