package org.mozilla.tiktokreporter.data.model

import org.mozilla.tiktokreporter.data.remote.response.OnboardingStepDTO

data class OnboardingStep(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val imageUrl: String,
    val details: String,
    val order: Int
)

fun OnboardingStepDTO.toOnboardingStep(): OnboardingStep {
    return OnboardingStep(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        imageUrl = imageUrl,
        details = details,
        order = order
    )
}