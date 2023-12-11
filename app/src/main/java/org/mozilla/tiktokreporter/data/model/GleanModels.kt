package org.mozilla.tiktokreporter.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.mozilla.tiktokreporter.data.remote.response.FormFieldDTO
import org.mozilla.tiktokreporter.data.remote.response.UploadedRecordingDTO

@JsonClass(generateAdapter = true)
data class GleanFormItem(
    @Json(name = "inputValue") val inputValue: Any,
    @Json(name = "formItem") val formItem: FormFieldDTO
)

@JsonClass(generateAdapter = true)
data class GleanReportLinkFormRequest(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "items") val items: List<GleanFormItem>,
)

@JsonClass(generateAdapter = true)
data class GleanRecordSessionFormRequest(
    @Json(name = "recordingInfo") val recordingInfo: UploadedRecordingDTO,
    @Json(name = "comments") val comments: String?
)