package org.mozilla.tiktokreporter.common.formcomponents

import org.mozilla.tiktokreporter.data.model.FormField

data class FormFieldUiComponent(
    val formField: FormField,
    val value: Any
)

fun FormField.toUiComponent(
    value: Any
): FormFieldUiComponent {
    return FormFieldUiComponent(
        formField = this,
        value = value
    )
}

fun List<FormField>.toUiComponents(): List<FormFieldUiComponent> {
    return this.mapNotNull { field ->
        when (field) {
            is FormField.TextField -> field.toUiComponent("")
            is FormField.DropDown -> {
                val selectedOption = field.options.firstOrNull { option -> option.id == field.selectedOptionId }
                field.toUiComponent(selectedOption?.title ?: "")
            }
            is FormField.Slider -> field.toUiComponent(0)
            else -> null
        }
    }
}