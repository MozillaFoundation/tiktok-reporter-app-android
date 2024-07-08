package org.mozilla.tiktokreporter.common

import org.mozilla.tiktokreporter.data.model.FormField

const val OTHER_CATEGORY_TEXT_FIELD_ID = "other_category_id"
const val OTHER_DROP_DOWN_OPTION_ID = "other_category_id"

sealed class FormFieldUiComponent<T>(
    open val id: String,
    open val value: T,
    open val isVisible: Boolean,
    open val isRequired: Boolean,
    open val readOnly: Boolean,
    open val description: String,
    open val label: String,
    open val error: FormFieldError? = null,
    open val edited: Boolean? = false
) {
    data class TextField(
        override val id: String,
        override val value: String,
        override val isVisible: Boolean,
        override val isRequired: Boolean,
        override val readOnly: Boolean,
        override val description: String,
        override val label: String,
        override val error: FormFieldError? = null,
        override val edited: Boolean? = false,
        val placeholder: String,
        val multiline: Boolean,
        val maxLines: Int,
        val isTikTokLink: Boolean? = false
    ) : FormFieldUiComponent<String>(id, value, isVisible, isRequired, readOnly, description, label)

    data class DropDown(
        override val id: String,
        override val value: String,
        override val isVisible: Boolean,
        override val isRequired: Boolean,
        override val readOnly: Boolean,
        override val description: String,
        override val label: String,
        override val error: FormFieldError? = null,
        override val edited: Boolean? = false,
        val options: List<OptionComponent>,
        val placeholder: String,
    ) : FormFieldUiComponent<String>(id, value, isVisible, isRequired, readOnly, description, label)

    data class Slider(
        override val id: String,
        override val value: Int,
        override val isVisible: Boolean,
        override val isRequired: Boolean,
        override val readOnly: Boolean,
        override val description: String,
        override val label: String,
        override val error: FormFieldError? = null,
        override val edited: Boolean? = false,
        val max: Int,
        val step: Int,
        val leftLabel: String,
        val rightLabel: String,
    ) : FormFieldUiComponent<Int>(id, value, isVisible, isRequired, readOnly, description, label)

    fun isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(value.toString()).matches()
    }
}

data class OptionComponent(
    val id: String, val title: String
)

sealed class FormFieldError {
    data object Empty : FormFieldError()
    data object EmptyCategory : FormFieldError()
    data object NoTikTokLink : FormFieldError()
    data object EmailInvalid : FormFieldError()
}

fun FormField.toFormFieldComponent(): List<FormFieldUiComponent<*>>? {
    return when (this) {
        is FormField.TextField -> listOf(
            FormFieldUiComponent.TextField(
                id = id,
                value = "",
                isVisible = true,
                isRequired = isRequired,
                readOnly = false,
                description = description,
                label = label,
                placeholder = placeholder,
                multiline = multiline,
                maxLines = maxLines,
                isTikTokLink = isTikTokLink
            )
        )

        is FormField.DropDown -> {
            val initialValue = this.options.firstOrNull { it.id == this.selectedOptionId }?.title.orEmpty()

            val dropDown = FormFieldUiComponent.DropDown(
                id = id,
                value = initialValue,
                isVisible = true,
                isRequired = isRequired,
                readOnly = false,
                description = description,
                label = label,
                options = buildList {
                    addAll(options.map {
                        OptionComponent(it.id, it.title)
                    })
                    if (this@toFormFieldComponent.hasOtherOption) add(
                        OptionComponent(OTHER_DROP_DOWN_OPTION_ID, "Other")
                    )
                },
                placeholder = placeholder,
            )
            val otherTextField = FormFieldUiComponent.TextField(
                id = "${id}_${OTHER_CATEGORY_TEXT_FIELD_ID}",
                value = "",
                isVisible = false,
                isRequired = isRequired,
                readOnly = false,
                description = "",
                label = "Suggest a category",
                placeholder = placeholder,
                multiline = false,
                maxLines = 1
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
            FormFieldUiComponent.Slider(
                id = id,
                value = 0,
                isVisible = true,
                isRequired = isRequired,
                readOnly = false,
                description = description,
                label = label,
                max = max,
                step = step,
                leftLabel = leftLabel,
                rightLabel = rightLabel
            )
        )

        else -> null
    }
}

fun List<FormField>.toUiComponents(
    tikTokUrl: String? = null
): List<FormFieldUiComponent<*>> {
    val indexOfFirstTextField = this.indexOfFirst { it is FormField.TextField }

    val components = this.mapIndexedNotNull { index, field ->
        val fieldComponent = field.toFormFieldComponent()

        if (tikTokUrl != null && index == indexOfFirstTextField) {
            fieldComponent?.map { textField ->
                textField as FormFieldUiComponent.TextField

                textField.copy(
                    readOnly = true, value = tikTokUrl
                )
            }
        } else {
            fieldComponent
        }
    }.flatten()

    return components
}