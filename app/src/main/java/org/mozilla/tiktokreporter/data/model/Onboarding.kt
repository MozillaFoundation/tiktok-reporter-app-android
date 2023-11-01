package org.mozilla.tiktokreporter.data.model

import org.mozilla.tiktokreporter.data.remote.response.OnboardingDTO

data class Onboarding(
    val id: String,
    val name: String,
    val steps: List<OnboardingStep>,
    val form: Form?
)

fun OnboardingDTO.toOnboarding(): Onboarding {
    return Onboarding(
        id = id,
        name = name,
        steps = stepDTOs.map { it.toOnboardingStep() },
        form = formDTO?.toForm()
    )
}