package org.mozilla.tiktokreporter.navigation

import org.mozilla.tiktokreporter.editvideo.EditVideoScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import kotlinx.coroutines.delay
import org.mozilla.tiktokreporter.aboutapp.AboutAppScreen
import org.mozilla.tiktokreporter.reportform.ReportFormScreen
import org.mozilla.tiktokreporter.settings.SettingsScreen
import org.mozilla.tiktokreporter.apppolicy.AppPolicyScreen
import org.mozilla.tiktokreporter.datahandling.DataHandlingScreen
import org.mozilla.tiktokreporter.email.EmailScreen
import org.mozilla.tiktokreporter.reportformcompleted.ReportFormCompletedScreen
import org.mozilla.tiktokreporter.splashscreen.SplashScreen
import org.mozilla.tiktokreporter.studieslist.StudiesListScreen
import org.mozilla.tiktokreporter.studyonboarding.StudyOnboardingScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavContainer(
    onboardingCompleted: Boolean,
    termsAccepted: Boolean
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { _ ->
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Destination.SplashScreen.route
        ) {
            addSplashScreen(
                navController = navController,
                onboardingCompleted = onboardingCompleted,
                termsAccepted = termsAccepted
            )
            addOnBoarding(
                navController = navController
            )
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

    val onNextScreen = {
        val destination = if (onboardingCompleted) {
            NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
        } else if (termsAccepted) {
            NestedDestination.Studies.createRoute(
                root = Destination.Onboarding
            )
        } else {
            NestedDestination.AppPolicy.createRouteWithArguments(
                root = Destination.Onboarding,
                type = NestedDestination.AppPolicy.Type.TermsAndConditions,
                isForOnboarding = true
            )
        }

        navController.navigate(destination) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

    navigation(
        route = Destination.SplashScreen.route,
        startDestination = startDestination
    ) {
        composable(
            route = startDestination
        ) {
            LaunchedEffect(Unit) {
                delay(2000L)
                onNextScreen()
            }
            SplashScreen()
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
        addTermsAndConditions(
            navController = navController,
            root = Destination.Onboarding
        )
        addStudies(
            navController = navController,
            root = Destination.Onboarding
        )
        addStudyOnboarding(
            navController = navController,
            root = Destination.Onboarding
        )
        addEmail(
            navController = navController,
            root = Destination.Onboarding
        )
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
        startDestination = destination,
    ) {
        addReportFormNested(navController)
        addReportSubmittedNested(navController)
        addEditVideoNested(navController)
    }
}

private fun NavGraphBuilder.addReportFormNested(
    navController: NavController
) {
    val startDestination = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
    composable(
        route = startDestination,
        arguments = NestedDestination.ReportFormNested.argumentsList
    ) {
        val onGoToSettings = {
            val destination = NestedDestination.SettingsNested.createRoute(Destination.Settings)
            navController.navigate(destination)
        }
        val onGoToReportSubmittedScreen = {
            val destination = NestedDestination.ReportSubmittedNested.createRoute(Destination.ReportForm)
            navController.navigate(destination)
        }
        val onGoToStudies = {
            val destination = NestedDestination.Studies.createRoute(Destination.Settings)
            navController.navigate(destination) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
        val onGoToEditVideo = {
            val destination = NestedDestination.EditVideo.createRoute(Destination.ReportForm)
            navController.navigate(destination)
        }

        ReportFormScreen(
            onGoToSettings = onGoToSettings,
            onGoToReportSubmittedScreen = onGoToReportSubmittedScreen,
            onGoToStudies = onGoToStudies,
            onGoToEditVideo = onGoToEditVideo,
            onGoBack = {
                navController.navigateUp()
            }
        )
    }
}

private fun NavGraphBuilder.addReportSubmittedNested(
    navController: NavController
) {
    val startDestination = NestedDestination.ReportSubmittedNested.createRoute(Destination.ReportForm)
    composable(
        route = startDestination
    ) {
        ReportFormCompletedScreen(
            onNavigateBack = { navController.navigateUp() },
            onGoToSettings = {
                val direction = NestedDestination.SettingsNested.createRoute(Destination.Settings)
                navController.navigate(direction) {
                    popUpTo(startDestination) {
                        inclusive = true
                    }
                }
            }
        )
    }
}

private fun NavGraphBuilder.addEditVideoNested(
    navController: NavController
) {
    val startDestination = NestedDestination.EditVideo.createRoute(Destination.ReportForm)
    composable(
        route = startDestination
    ) {
        EditVideoScreen(
            onNavigateBack = { navController.navigateUp() }
        )
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
        addTermsAndConditions(
            navController = navController,
            root = Destination.Settings
        )
        addStudies(
            navController = navController,
            root = Destination.Settings
        )
        addEmail(
            navController = navController,
            root = Destination.Settings
        )
        addStudyOnboarding(
            navController = navController,
            root = Destination.Settings
        )
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
        val destination = NestedDestination.AppPolicy.createRouteWithArguments(Destination.Settings, type, false)
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
    val onGoToStudies = {
        val destination = NestedDestination.Studies.createRoute(Destination.Onboarding)
        navController.navigate(destination) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }
    val onGoToStudyOnboarding = {
        val destination = NestedDestination.StudyOnboarding.createRoute(Destination.Onboarding)
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
            onGoToStudies = onGoToStudies,
            onGoToStudyOnboarding = onGoToStudyOnboarding,
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
    val onGoToStudyTerms = {
        val destination = NestedDestination.AppPolicy.createRouteWithArguments(
            root = Destination.Onboarding,
            type = NestedDestination.AppPolicy.Type.Study,
            isForOnboarding = true
        )
        navController.navigate(destination) {
            popUpTo(0)
        }
    }
    val onGoToEmail = {
        val destination = NestedDestination.Email.createRoute(
            root = Destination.Onboarding
        )
        navController.navigate(destination) {
            popUpTo(0)
        }
    }
    val onGoToReportForm = {
        val destination = NestedDestination.ReportFormNested.createRoute(
            root = Destination.ReportForm
        )
        navController.navigate(destination) {
            popUpTo(0)
        }
    }

    composable(
        route = startDestination
    ) {
        StudiesListScreen(
            onGoToStudyOnboarding = onGoToStudyOnboarding,
            onGoToStudyTerms = onGoToStudyTerms,
            onGoToEmail = onGoToEmail,
            onGoToReportForm = onGoToReportForm,
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