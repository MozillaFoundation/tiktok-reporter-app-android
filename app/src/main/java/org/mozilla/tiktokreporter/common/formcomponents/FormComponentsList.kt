package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.common.FormFieldUiComponent

fun LazyListScope.formComponentsItems(
    formFields: List<FormFieldUiComponent<*>>,
    onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit
) {
    items(formFields.filter { it.isVisible }) { field ->
        when (field) {
            is FormFieldUiComponent.TextField -> {
                FormTextField(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = field,
                    onTextChanged = {
                        onFormFieldValueChanged(field.id, it)
                    }
                )
            }

            is FormFieldUiComponent.DropDown -> {
                FormDropDown(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = field,
                    onOptionChanged = {
                        onFormFieldValueChanged(field.id, it)
                    }
                )
            }

            is FormFieldUiComponent.Slider -> {
                FormSlider(
                    modifier = Modifier.fillParentMaxWidth(),
                    field = field,
                    onValueChanged = {
                        onFormFieldValueChanged(field.id, it)
                    }
                )
            }
        }
    }
}