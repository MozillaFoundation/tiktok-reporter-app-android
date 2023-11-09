package org.mozilla.tiktokreporter.common

import org.mozilla.tiktokreporter.data.model.FormField

const val OTHER_CATEGORY_TEXT_FIELD_ID = "other_category_id"
const val OTHER_DROP_DOWN_OPTION_ID = "other_category_id"

data class FormFieldComponent(
    val id: String,
    val isVisible: Boolean,
    val isRequired: Boolean,
    val description: String,
    val label: String,

    val field: FormFieldUiComponent<*>
)

sealed class FormFieldUiComponent<T>(
    open val value: T
) {
    data class TextField(
        override val value: String,
        val readOnly: Boolean,
        val placeholder: String,
        val multiline: Boolean,
        val maxLines: Int,
    ) : FormFieldUiComponent<String>(value)

    data class DropDown(
        override val value: String,
        val options: List<OptionComponent>,
        val placeholder: String,
    ) : FormFieldUiComponent<String>(value)

    data class Slider(
        override val value: Int,
        val max: Int,
        val step: Int,
        val leftLabel: String,
        val rightLabel: String,
    ) : FormFieldUiComponent<Int>(value)
}

data class OptionComponent(
    val id: String,
    val title: String
)

fun FormField.toFormFieldComponent(): List<FormFieldComponent>? {
    return when (this) {
        is FormField.TextField -> listOf(
            FormFieldComponent(
                id = id,
                isVisible = true,
                isRequired = isRequired,
                description = description,
                label = label,
                field = FormFieldUiComponent.TextField(
                    value = "",
                    readOnly = false,
                    placeholder = placeholder,
                    multiline = multiline,
                    maxLines = maxLines
                )
            )
        )

        is FormField.DropDown -> {
            val initialValue =
                this.options.firstOrNull { it.id == this.selectedOptionId }?.title.orEmpty()

            val dropDown = FormFieldComponent(
                id = id,
                isVisible = true,
                isRequired = isRequired,
                description = description,
                label = label,
                field = FormFieldUiComponent.DropDown(
                    value = initialValue,
                    options = buildList {
                        addAll(
                            options.map {
                                OptionComponent(it.id, it.title)
                            }
                        )
                        if (this@toFormFieldComponent.hasOtherOption)
                            add(
                                OptionComponent(OTHER_DROP_DOWN_OPTION_ID, "Other")
                            )
                    },
                    placeholder = placeholder,
                )
            )

            val otherTextField = FormFieldComponent(
                id = OTHER_CATEGORY_TEXT_FIELD_ID,
                isVisible = false,
                isRequired = isRequired,
                description = "",
                label = "Suggest a category",
                field = FormFieldUiComponent.TextField(
                    value = "",
                    readOnly = false,
                    placeholder = placeholder,
                    multiline = false,
                    maxLines = 1
                )
            )

            buildList {
                add(dropDown)
                if (this@toFormFieldComponent.hasOtherOption) {
                    add(
                        otherTextField
                    )
                }
            }
        }

        is FormField.Slider -> listOf(
            FormFieldComponent(
                id = id,
                isVisible = true,
                isRequired = isRequired,
                description = description,
                label = label,
                field = FormFieldUiComponent.Slider(
                    value = 0,
                    max = max,
                    step = step,
                    leftLabel = leftLabel,
                    rightLabel = rightLabel
                )
            )
        )

        else -> null
    }
}

fun List<FormField>.toUiComponents(): List<FormFieldComponent> {
    val components = this.mapNotNull { field ->
        field.toFormFieldComponent()
    }.flatten()

    return components
}