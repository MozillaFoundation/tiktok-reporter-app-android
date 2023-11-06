package org.mozilla.tiktokreporter.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import org.mozilla.tiktokreporter.aboutapp.AboutAppScreen
import org.mozilla.tiktokreporter.reportform.ReportFormScreen
import org.mozilla.tiktokreporter.settings.SettingsScreen
import org.mozilla.tiktokreporter.apppolicy.AppPolicyScreen
import org.mozilla.tiktokreporter.datahandling.DataHandlingScreen
import org.mozilla.tiktokreporter.email.EmailScreen
import org.mozilla.tiktokreporter.splashscreen.SplashScreen
import org.mozilla.tiktokreporter.studieslist.StudiesListScreen
import org.mozilla.tiktokreporter.studyonboarding.StudyOnboardingScreen
import org.mozilla.tiktokreporter.util.emptyCallback

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavContainer(
    onboardingCompleted: Boolean,
    termsAccepted: Boolean
) {
    val navController = rememberNavController()
    navController.addOnDestinationChangedListener { controller, destination, _ ->
        val list = controller.currentBackStack.value.map {it.destination.route } + controller.currentBackStackEntry?.destination?.route
        Log.d("Backstack", list.joinToString("\n"))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { _ ->
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Destination.SplashScreen.route
        ) {
            addSplashScreen(navController, onboardingCompleted, termsAccepted)
            addOnBoarding(navController)
            addReportForm(navController)
            addSettings(navController)
        }
    }
}


/**
 * SPLASH SCREEN
 */
private fun NavGraphBuilder.addSplashScreen(
    navController: NavController,
    onboardingCompleted: Boolean,
    termsAccepted: Boolean
) {
    val startDestination =
        NestedDestination.SplashScreenNested.createRoute(Destination.SplashScreen)
    navigation(
        route = Destination.SplashScreen.route,
        startDestination = startDestination
    ) {
        composable(
            route = startDestination
        ) {
            SplashScreen(
                onNextScreen = {
                    val destination = if (onboardingCompleted) {
                        NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
                    } else if (termsAccepted) {
                        NestedDestination.Studies.createRoute(
                            root = Destination.Onboarding
                        )
                    } else {
                        NestedDestination.AppPolicy.createRouteWithArguments(
                            root = Destination.Onboarding,
                            type = NestedDestination.AppPolicy.Type.TermsAndConditions
                        )
                    }

                    navController.navigate(destination) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}


/**
 * ONBOARDING
 */
private fun NavGraphBuilder.addOnBoarding(
    navController: NavController
) {
    val destination = NestedDestination.AppPolicy.createRoute(Destination.Onboarding)
    navigation(
        route = Destination.Onboarding.route,
        startDestination = destination
    ) {
        addTermsAndConditions(navController, Destination.Onboarding)
        addStudies(navController, Destination.Onboarding)
        addStudyOnboarding(navController, Destination.Onboarding)
        addEmail(navController, Destination.Onboarding)
    }
}


/**
 * REPORT FORM
 */
private fun NavGraphBuilder.addReportForm(
    navController: NavController
) {
    val destination = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
    navigation(
        route = Destination.ReportForm.route,
        startDestination = destination
    ) {
        addReportFormNested(navController)
        addReportSubmittedNested()
    }
}

private fun NavGraphBuilder.addReportFormNested(
    navController: NavController
) {
    val startDestination = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
    composable(
        route = startDestination
    ) {
        val onGoToSettings = {
            val destination = NestedDestination.SettingsNested.createRoute(Destination.Settings)
            navController.navigate(destination)
        }
        val onGoToReportSubmittedScreen = { }
        val onGoToStudies = {
            val destination = NestedDestination.Studies.createRoute(Destination.Settings)
            navController.navigate(destination) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }

        ReportFormScreen(
            onGoToSettings = onGoToSettings,
            onGoToReportSubmittedScreen = onGoToReportSubmittedScreen,
            onGoToStudies = onGoToStudies
        )
    }
}

private fun NavGraphBuilder.addReportSubmittedNested() {
    composable(
        route = NestedDestination.ReportSubmittedNested.createRoute(Destination.ReportForm)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Report submitted")
        }

    }
}


/**
 * SETTINGS
 */
private fun NavGraphBuilder.addSettings(
    navController: NavController
) {
    val destination = NestedDestination.SettingsNested.createRoute(Destination.Settings)
    navigation(
        route = Destination.Settings.route,
        startDestination = destination
    ) {
        addSettingsNested(navController)
        addAboutApp(navController)
        addTermsAndConditions(navController, Destination.Settings)
        addStudies(navController, Destination.Settings)
        addEmail(navController, Destination.Settings)
        addStudyOnboarding(navController, Destination.Settings)
        addDataHandling(navController)
    }
}

private fun NavGraphBuilder.addSettingsNested(
    navController: NavController
) {
    val onGoToAppPurpose = {
        val destination = NestedDestination.AboutApp.createRoute(Destination.Settings)
        navController.navigate(destination)
    }
    val onGoToStudies = {
        val destination = NestedDestination.Studies.createRoute(Destination.Settings)
        navController.navigate(destination)
    }
    val onGoToEmail = {
        val destination = NestedDestination.Email.createRoute(Destination.Settings)
        navController.navigate(destination)
    }
    val onGoToAppPolicy: (NestedDestination.AppPolicy.Type) -> Unit = { type ->
        val destination = NestedDestination.AppPolicy.createRouteWithArguments(Destination.Settings, type)
        navController.navigate(destination)
    }
    val onGoToDataHandling = {
        val destination = NestedDestination.DataHandling.createRoute(Destination.Settings)
        navController.navigate(destination)
    }

    composable(
        route = NestedDestination.SettingsNested.createRoute(Destination.Settings)
    ) {
        SettingsScreen(
            onNavigateBack = {
                navController.navigateUp()
            },
            onGoToAppPurpose = onGoToAppPurpose,
            onGoToStudies = onGoToStudies,
            onGoToEmail = onGoToEmail,
            onGoToTermsAndConditions = {
                onGoToAppPolicy(NestedDestination.AppPolicy.Type.TermsAndConditions)
            },
            onGoToPrivacyPolicy = {
                onGoToAppPolicy(NestedDestination.AppPolicy.Type.PrivacyPolicy)
            },
            onGoToDataHandling = onGoToDataHandling,
        )
    }
}

private fun NavGraphBuilder.addAboutApp(
    navController: NavController
) {
    composable(
        route = NestedDestination.AboutApp.createRoute(Destination.Settings)
    ) {
        AboutAppScreen(
            onNavigateBack = {
                navController.navigateUp()
            }
        )
    }
}

private fun NavGraphBuilder.addTermsAndConditions(
    navController: NavController,
    root: Destination
) {
    val startDestination = NestedDestination.AppPolicy.createRoute(root)

    val isForOnboarding = root is Destination.Onboarding
    val onNextScreen = {
        val destination = NestedDestination.Studies.createRoute(Destination.Onboarding)
        navController.navigate(destination) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

    composable(
        route = startDestination,
        arguments = NestedDestination.AppPolicy.argumentsList
    ) {
        AppPolicyScreen(
            isForOnboarding = isForOnboarding,
            onNextScreen = if (isForOnboarding) onNextScreen else emptyCallback,
            onNavigateBack = {
                navController.navigateUp()
            }
        )
    }
}

private fun NavGraphBuilder.addStudies(
    navController: NavController,
    root: Destination
) {
    val startDestination = NestedDestination.Studies.createRoute(root)

    val onGoToStudyOnboarding = {
        val destination = NestedDestination.StudyOnboarding.createRoute(Destination.Onboarding)
        navController.navigate(destination) {
            popUpTo(0)
        }
    }

    composable(
        route = startDestination
    ) {
        StudiesListScreen(
            onNextScreen = onGoToStudyOnboarding,
            isForOnboarding = root is Destination.Onboarding,
            onNavigateBack = {
                navController.navigateUp()
            }
        )
    }
}

private fun NavGraphBuilder.addStudyOnboarding(
    navController: NavController,
    root: Destination
) {
    val startedFromSettings = root is Destination.Settings

    val startDestination = NestedDestination.StudyOnboarding.createRoute(root)

    val nextDestination = if (startedFromSettings) {
        NestedDestination.ReportFormNested.createRoute(Destination.Onboarding)
    } else {
        NestedDestination.Email.createRoute(Destination.Onboarding)
    }
    val onNextScreen = {
        navController.navigate(nextDestination) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

    composable(
        route = startDestination
    ) {
        StudyOnboardingScreen(
            onNextScreen = onNextScreen
        )
    }
}

private fun NavGraphBuilder.addEmail(
    navController: NavController,
    root: Destination
) {
    val startDestination = NestedDestination.Email.createRoute(root)
    val isForOnboarding = root is Destination.Onboarding

    composable(
        route = startDestination
    ) {
        EmailScreen(
            isForOnboarding = isForOnboarding,
            onNextScreen = {
                val destination = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
                navController.navigate(destination) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            },
            onNavigateBack = {
                navController.navigateUp()
            }
        )
    }
}

private fun NavGraphBuilder.addDataHandling(
    navController: NavController
) {
    composable(
        route = NestedDestination.DataHandling.createRoute(Destination.Settings)
    ) {
        DataHandlingScreen(
            onNavigateBack = {
                navController.navigateUp()
            }
        )
    }
}