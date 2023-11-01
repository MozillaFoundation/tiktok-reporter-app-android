package org.mozilla.tiktokreporter.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudyDTO(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "isActive") val isActive: Boolean,
    @Json(name = "policies") val policyDTOs: List<PolicyDTO>,
    @Json(name = "onboarding") val onboardingDTO: OnboardingDTO?
)