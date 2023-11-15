package org.mozilla.tiktokreporter.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

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

    open fun createRoute(root: Destination): String = "${root.route}/$route"

    data object SplashScreenNested: NestedDestination("splashScreenNested")


    data object AppPolicy: NestedDestination("appPolicy/{type}/{isForOnboarding}") {
        enum class Type {
            TermsAndConditions,
            PrivacyPolicy,
            Study;
        }

        val argumentsList = listOf(
            navArgument("type") {
                type = NavType.StringType
                nullable = false
            },
            navArgument("isForOnboarding") {
                type = NavType.BoolType
                nullable = false
            }
        )

        data class Args(
            val type: Type,
            val isForOnboarding: Boolean
        )

        fun createRouteWithArguments(
            root: Destination,
            type: Type,
            isForOnboarding: Boolean,
        ): String = "${root.route}/appPolicy/${type.name}/${isForOnboarding}"

        fun parseArguments(
            backStackEntry: NavBackStackEntry
        ): Args = Args(
            type = Type.valueOf(backStackEntry.arguments?.getString("type").orEmpty()),
            isForOnboarding = backStackEntry.arguments?.getBoolean("isForOnboarding") ?: true
        )
    }
    data object Studies: NestedDestination("studies")
    data object StudyOnboarding: NestedDestination("studyOnboarding")
    data object Email: NestedDestination("email")
    data object DataHandling: NestedDestination("dataHandling")
    data object AboutApp: NestedDestination("aboutApp")


    data object ReportFormNested: NestedDestination("reportForm/{tikTokUrl}") {
        val argumentsList = listOf(
            navArgument("tikTokUrl") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )

        data class Args(
            val tikTokUrl: String
        )

        override fun createRoute(
            root: Destination
        ): String = "${root.route}/reportForm/${null}"
        fun createRouteWithArguments(
            root: Destination,
            tikTokUrl: String
        ): String = "${root.route}/reportForm/$tikTokUrl"

        fun parseArguments(
            backStackEntry: NavBackStackEntry
        ): Args = Args(
            tikTokUrl = backStackEntry.arguments?.getString("tikTokUrl").orEmpty()
        )
    }
    data object ReportSubmittedNested: NestedDestination("reportSubmitted")
    data object SettingsNested: NestedDestination("settingsHome")

}