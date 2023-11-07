package org.mozilla.tiktokreporter.data.model

import org.mozilla.tiktokreporter.data.remote.response.StudyDTO

data class StudyOverview(
    val id: String,
    val name: String,
    val description: String,
    val isActive: Boolean,
    val isSelected: Boolean,
    val hasOnboarding: Boolean = false,
    val hasEmailForm: Boolean = false,
    val hasPolicies: Boolean = false
)

fun StudyDTO.toStudyOverview(
    isSelected: Boolean = false
): StudyOverview {
    return StudyOverview(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        isSelected = isSelected,
        hasOnboarding = onboardingDTO != null,
        hasEmailForm = onboardingDTO?.formDTO != null,
        hasPolicies = policyDTOs.isNotEmpty()
    )
}

data class StudyDetails(
    val id: String,
    val name: String,
    val description: String,
    val isActive: Boolean,
    val onboarding: Onboarding?,
    val policies: List<Policy>,
    val form: Form?,
    val supportsRecording: Boolean
)

fun StudyDTO.toStudyDetails(): StudyDetails {
    return StudyDetails(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        onboarding = onboardingDTO?.toOnboarding(),
        policies = policyDTOs.map { it.toPolicy() },
        form = formDTO?.toForm(),
        supportsRecording = supportsRecording
    )
}