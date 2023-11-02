package org.mozilla.tiktokreporter.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import org.mozilla.tiktokreporter.MainViewModel
import org.mozilla.tiktokreporter.aboutapp.AboutAppScreen
import org.mozilla.tiktokreporter.reportform.ReportFormScreen
import org.mozilla.tiktokreporter.settings.SettingsScreen
import org.mozilla.tiktokreporter.apppolicy.AppPolicyScreen
import org.mozilla.tiktokreporter.datahandling.DataHandlingScreen
import org.mozilla.tiktokreporter.splashscreen.SplashScreen
import org.mozilla.tiktokreporter.studieslist.StudiesListScreen
import org.mozilla.tiktokreporter.studyonboarding.StudyOnboardingScreen
import org.mozilla.tiktokreporter.util.emptyCallback

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavContainer() {

    val viewModel = hiltViewModel<MainViewModel>()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    navController.addOnDestinationChangedListener { _, destination, _ ->
        println("New Destination: $destination")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { _ ->
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Destination.SplashScreen.route
        ) {
            addSplashScreen(navController, onboardingCompleted)
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
    onboardingCompleted: Boolean
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
                    } else {
                        NestedDestination.AppPolicy.createRouteWithArguments(Destination.Onboarding, type = NestedDestination.AppPolicy.Type.TermsAndConditions)
                    }

                    navController.navigate(destination) {
                        launchSingleTop = true
                        popUpTo(startDestination) {
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
    composable(
        route = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
    ) {
        val onGoToSettings = {
            val destination = NestedDestination.SettingsNested.createRoute(Destination.Settings)
            navController.navigate(destination)
        }
        val onGoToReportSubmittedScreen = { }

        ReportFormScreen(
            onGoToSettings = onGoToSettings,
            onGoToReportSubmittedScreen = onGoToReportSubmittedScreen
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
        addEmail(navController, Destination.Onboarding)
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
    val onGoToStudies = { }
    val onGoToEmail = { }
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
    val isForOnboarding = root is Destination.Onboarding
    val onNextScreen = {
        val destination = NestedDestination.Studies.createRoute(Destination.Onboarding)
        navController.navigate(destination)
    }

    composable(
        route = NestedDestination.AppPolicy.createRoute(root),
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
    val onGoToStudyOnboarding = {
        val destination = NestedDestination.StudyOnboarding.createRoute(Destination.Onboarding)
        navController.navigate(destination)
    }

    composable(
        route = NestedDestination.Studies.createRoute(root)
    ) {
        StudiesListScreen(
            onNextScreen = onGoToStudyOnboarding
        )
    }
}

private fun NavGraphBuilder.addEmail(
    navController: NavController,
    root: Destination
) {
    composable(
        route = NestedDestination.Email.createRoute(root)
    ) {
        when (root) {
            is Destination.Onboarding -> {
                // email onboarding screen
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Onboarding email")
                }
            }

            is Destination.Settings -> {
                // email settings screen
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Settings email")
                }
            }

            else -> Unit
        }
    }
}

private fun NavGraphBuilder.addStudyOnboarding(
    navController: NavController,
    root: Destination
) {
    val onGoToEmailForm = {
        val destination = NestedDestination.Email.createRoute(Destination.Onboarding)
        navController.navigate(destination)
    }
    val onGoToReportForm = {
        val destination = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
        navController.navigate(destination)
    }
    composable(
        route = NestedDestination.StudyOnboarding.createRoute(root)
    ) {
        StudyOnboardingScreen(
            onGoToEmailForm = onGoToEmailForm,
            onGoToReportForm = onGoToReportForm
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