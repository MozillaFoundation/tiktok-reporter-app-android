package org.mozilla.tiktokreporter.data.model

import org.mozilla.tiktokreporter.data.remote.response.FormDTO
import org.mozilla.tiktokreporter.data.remote.response.FormFieldDTO
import org.mozilla.tiktokreporter.data.remote.response.OptionDTO

data class Form(
    val id: String,
    val name: String,
    val fields: List<FormField>
)

sealed class FormField(
    open val id: String,
    open val isRequired: Boolean,
    val type: FormFieldType
) {
    data class TextField(
        override val id: String,
        override val isRequired: Boolean,
        val label: String,
        val placeholder: String,
        val multiline: Boolean,
        val maxLines: Int,
        val description: String
    ): FormField(id, isRequired, FormFieldType.TextField)

    data class DropDown(
        override val id: String,
        override val isRequired: Boolean,
        val label: String,
        val options: List<Option>,
        val selected: String,
        val description: String,
        val placeholder: String,
        val hasOtherOption: Boolean
    ): FormField(id, isRequired, FormFieldType.DropDown)

    data class Slider(
        override val id: String,
        override val isRequired: Boolean,
        val max: Int,
        val step: Int,
        val label: String,
        val leftLabel: String,
        val rightLabel: String,
        val description: String
    ): FormField(id, isRequired, FormFieldType.Slider)

    data object CheckboxGroup: FormField("checkbox_group", false, FormFieldType.CheckboxGroup)
    data object RadioGroup: FormField("radio_group", false, FormFieldType.RadioGroup)
    data object MultiSelect: FormField("multi_select", false, FormFieldType.MultiSelect)
    data object Unknown: FormField("unknown", false, FormFieldType.Unknown)
}

data class Option(
    val id: String,
    val title: String
)

enum class FormFieldType {
    TextField,
    Slider,
    DropDown,
    MultiSelect,
    RadioGroup,
    CheckboxGroup,
    Unknown
}


fun FormDTO.toForm(): Form {
    return Form(
        id = id,
        name = name,
        fields = fields.map {
            it.toFormField()
        }
    )
}

fun FormFieldDTO.toFormField(): FormField {
    return when(this) {
        is FormFieldDTO.TextField -> FormField.TextField(
            id = id,
            isRequired = isRequired,
            label = label,
            placeholder = placeholder,
            multiline = multiline,
            maxLines = maxLines,
            description = description
        )
        is FormFieldDTO.DropDown -> FormField.DropDown(
            id = id,
            isRequired = isRequired,
            label = label,
            options = optionDTOs.map { it.toOption() },
            selected = selected,
            description = description,
            placeholder = placeholder,
            hasOtherOption = hasOtherOption
        )
        is FormFieldDTO.Slider -> FormField.Slider(
            id = id,
            isRequired = isRequired,
            max = max,
            step = step,
            label = label,
            leftLabel = leftLabel,
            rightLabel = rightLabel,
            description = description
        )
        is FormFieldDTO.CheckboxGroup -> FormField.CheckboxGroup
        is FormFieldDTO.MultiSelect -> FormField.MultiSelect
        is FormFieldDTO.RadioGroup -> FormField.RadioGroup
        FormFieldDTO.Unknown -> FormField.Unknown
    }
}

fun OptionDTO.toOption(): Option {
    return Option(
        id = id,
        title = title
    )
}