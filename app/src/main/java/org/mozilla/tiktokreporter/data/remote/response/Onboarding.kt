package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Onboarding(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "steps") val steps: List<OnboardingStep>,
    @Json(name = "form") val form: Form,
    @Json(name = "createdAt") val createdAt: LocalDateTime,
    @Json(name = "updatesAt") val updatesAt: LocalDateTime
)