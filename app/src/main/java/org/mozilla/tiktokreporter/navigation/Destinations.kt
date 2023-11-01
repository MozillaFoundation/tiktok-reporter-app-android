package org.mozilla.tiktokreporter.navigation

sealed class Destination(
    val route: String
) {
    companion object {
        const val FULL_SCREEN = "full-screen"
    }

    data object SplashScreen: Destination("splashscreen_$FULL_SCREEN")
    data object Onboarding: Destination("onboarding_$FULL_SCREEN")
    data object ReportForm: Destination("reportForm_$FULL_SCREEN")
    data object Settings: Destination("settings_$FULL_SCREEN")
}


sealed class NestedDestination(
    private val route: String
) {

    fun createRoute(root: Destination): String = "${root.route}/$route"

    data object SplashScreenNested: NestedDestination("splashScreenNested")


    data object TermsAndConditions: NestedDestination("onboardingTermsAndConditions")
    data object Studies: NestedDestination("studies")
    data object StudyOnboarding: NestedDestination("studyOnboarding")
    data object Email: NestedDestination("email")
    data object PrivacyPolicy: NestedDestination("privacyPolicy")
    data object DataHandling: NestedDestination("dataHandling")


    data object ReportFormNested: NestedDestination("reportForm")
    data object ReportSubmittedNested: NestedDestination("reportSubmitted")
    data object SettingsNested: NestedDestination("settingsHome")

}