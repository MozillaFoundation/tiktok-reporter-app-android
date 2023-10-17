package org.mozilla.tiktokreporter.navigation

sealed class Destination(
    val route: String
) {
    companion object {
        const val FULL_SCREEN = "full-screen"
    }

    object Onboarding: Destination("onboarding_$FULL_SCREEN")
    object ReportLink: Destination("reportLink")
    object RecordSession: Destination("recordSession")
    object Settings: Destination("Settings")
}


sealed class NestedDestination(
    private val route: String
) {

    fun createRoute(root: Destination): String = "${root.route}/$route"

    object TermsAndConditions: NestedDestination("onboardingTermsAndConditions")
    object Studies: NestedDestination("onboardingStudies")
    object Email: NestedDestination("onboardingEmail")
    object PrivacyPolicy: NestedDestination("settingsPrivacyPolicy")
    object DataHandling: NestedDestination("settingsDataHandling")


    object ReportLinkNested: NestedDestination("reportLink")
    object RecordSessionNested: NestedDestination("recordSession")
    object SettingsNested: NestedDestination("settingsHome")

}