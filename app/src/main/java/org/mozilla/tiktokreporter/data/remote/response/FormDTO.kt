package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.mozilla.tiktokreporter.data.model.FormField
import org.mozilla.tiktokreporter.data.model.Option
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class FormDTO(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "fields") val fields: List<FormFieldDTO>,
    @Json(name = "createdAt") val createdAt: LocalDateTime,
    @Json(name = "updatedAt") val updatedAt: LocalDateTime
)

sealed class FormFieldDTO(
    @Json(name = "id") open val id: String,
    @Json(name = "isRequired") open val isRequired: Boolean,
    @Json(name = "type") val type: FormFieldTypeDTO
) {
    @JsonClass(generateAdapter = true)
    data class TextField(
        @Json(name = "id") override val id: String,
        @Json(name = "isRequired") override val isRequired: Boolean,
        @Json(name = "label") val label: String,
        @Json(name = "placeholder") val placeholder: String,
        @Json(name = "multiline") val multiline: Boolean,
        @Json(name = "maxLines") val maxLines: Int,
        @Json(name = "description") val description: String,
        @Json(name = "isTikTokLink") val isTikTokLink: Boolean?
    ) : FormFieldDTO(id, isRequired, FormFieldTypeDTO.TextField)

    @JsonClass(generateAdapter = true)
    data class DropDown(
        @Json(name = "id") override val id: String,
        @Json(name = "isRequired") override val isRequired: Boolean,
        @Json(name = "label") val label: String,
        @Json(name = "options") val optionDTOs: List<OptionDTO>,
        @Json(name = "selected") val selectedOptionId: String,
        @Json(name = "description") val description: String,
        @Json(name = "placeholder") val placeholder: String,
        @Json(name = "hasOtherOption") val hasOtherOption: Boolean
    ) : FormFieldDTO(id, isRequired, FormFieldTypeDTO.DropDown)

    @JsonClass(generateAdapter = true)
    data class Slider(
        @Json(name = "id") override val id: String,
        @Json(name = "isRequired") override val isRequired: Boolean,
        @Json(name = "max") val max: Int,
        @Json(name = "step") val step: Int,
        @Json(name = "label") val label: String,
        @Json(name = "leftLabel") val leftLabel: String,
        @Json(name = "rightLabel") val rightLabel: String,
        @Json(name = "description") val description: String
    ) : FormFieldDTO(id, isRequired, FormFieldTypeDTO.Slider)

    @JsonClass(generateAdapter = true)
    data class CheckboxGroup(
        @Json(name = "id") override val id: String, @Json(name = "isRequired") override val isRequired: Boolean
    ) : FormFieldDTO(id, isRequired, FormFieldTypeDTO.CheckboxGroup)

    @JsonClass(generateAdapter = true)
    data class RadioGroup(
        @Json(name = "id") override val id: String, @Json(name = "isRequired") override val isRequired: Boolean
    ) : FormFieldDTO(id, isRequired, FormFieldTypeDTO.RadioGroup)

    @JsonClass(generateAdapter = true)
    data class MultiSelect(
        @Json(name = "id") override val id: String, @Json(name = "isRequired") override val isRequired: Boolean
    ) : FormFieldDTO(id, isRequired, FormFieldTypeDTO.MultiSelect)

    data object Unknown : FormFieldDTO("unknown", false, FormFieldTypeDTO.Unknown)
}

@JsonClass(generateAdapter = true)
data class OptionDTO(
    @Json(name = "id") val id: String, @Json(name = "title") val title: String
)


enum class FormFieldTypeDTO {
    TextField, DropDown, MultiSelect, RadioGroup, CheckboxGroup, Slider, Unknown
}

fun Option.toOptionDTO(): OptionDTO = OptionDTO(id, title)
fun FormField.toFormFieldDTO(): FormFieldDTO {
    return when (this) {
        is FormField.TextField -> FormFieldDTO.TextField(
            id, isRequired, label, placeholder, multiline, maxLines, description, isTikTokLink
        )

        is FormField.DropDown -> FormFieldDTO.DropDown(
            id, isRequired, label, options.map { it.toOptionDTO() }, selectedOptionId, description, placeholder, hasOtherOption
        )

        is FormField.Slider -> FormFieldDTO.Slider(
            id, isRequired, max, step, label, leftLabel, rightLabel, description
        )

        FormField.CheckboxGroup -> FormFieldDTO.CheckboxGroup(id, isRequired)
        FormField.MultiSelect -> FormFieldDTO.MultiSelect(id, isRequired)
        FormField.RadioGroup -> FormFieldDTO.RadioGroup(id, isRequired)
        FormField.Unknown -> FormFieldDTO.Unknown
    }
}

