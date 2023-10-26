package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Form(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "fields") val fields: List<FormField>,
    @Json(name = "createdAt") val createdAt: LocalDateTime,
    @Json(name = "updatedAt") val updatedAt: LocalDateTime
)

sealed class FormField(
    @Json(name = "id") open val id: String,
    @Json(name = "isRequired") open val isRequired: Boolean,
    @Json(name = "type") val type: FormFieldType
) {
    @JsonClass(generateAdapter = true)
    data class TextField(
        @Json(name = "id") override val id: String,
        @Json(name = "isRequired") override val isRequired: Boolean,
        @Json(name = "label") val label: String,
        @Json(name = "placeholder") val placeholder: String,
        @Json(name = "multiline") val multiline: Boolean,
        @Json(name = "maxLines") val maxLines: Int,
        @Json(name = "description") val description: String
    ): FormField(id, isRequired, FormFieldType.TextField)
    @JsonClass(generateAdapter = true)
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

    @JsonClass(generateAdapter = true)
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

    data object CheckboxGroup: FormField("checkbox_group", false, FormFieldType.Unknown)
    data object RadioGroup: FormField("radio_group", false, FormFieldType.Unknown)
    data object MultiSelect: FormField("multi_select", false, FormFieldType.Unknown)
    data object Unknown: FormField("unknown", false, FormFieldType.Unknown)
}

@JsonClass(generateAdapter = true)
data class Option(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String
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