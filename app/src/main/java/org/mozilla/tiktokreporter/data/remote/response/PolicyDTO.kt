package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class PolicyDTO(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: Type,
    @Json(name = "title") val title: String,
    @Json(name = "subtitle") val subtitle: String,
    @Json(name = "text") val text: String,
    @Json(name = "createdAt") val createdAt: LocalDateTime,
    @Json(name = "updatedAt") val updatedAt: LocalDateTime
) {
    @JsonClass(generateAdapter = false)
    enum class Type {
        PrivacyPolicy,
        TermsOfService
    }
}