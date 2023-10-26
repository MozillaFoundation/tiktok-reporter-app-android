package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Study(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "isActive") val isActive: Boolean,
    @Json(name = "policies") val policies: List<Policy>,
    @Json(name = "onboarding") val onboarding: Onboarding
)