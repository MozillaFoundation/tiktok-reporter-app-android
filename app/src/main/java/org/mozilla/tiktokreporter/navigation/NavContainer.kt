package org.mozilla.tiktokreporter.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import org.mozilla.tiktokreporter.onboarding.termsconditions.TermsAndConditionsScreen

@Composable
fun NavContainer() {

    val navController = rememberNavController()
    navController.addOnDestinationChangedListener { navController, destination, bundle ->
        println("New Destination: $destination")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = Destination.Onboarding.route
        ) {
            addSplashScreen(navController)
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
    navController: NavController
) {
    val destination = NestedDestination.SplashScreenNested.createRoute(Destination.SplashScreen)
    navigation(
        route = Destination.SplashScreen.route,
        startDestination = destination
    ) {
        composable(
            route = destination
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Splash screen")
            }
        }
    }
}


/**
 * ONBOARDING
 */
private fun NavGraphBuilder.addOnBoarding(
    navController: NavController
) {
    val destination = NestedDestination.TermsAndConditions.createRoute(Destination.Onboarding)
    navigation(
        route = Destination.Onboarding.route,
        startDestination = destination
    ) {
        addTermsAndConditions(navController, Destination.Onboarding)
        addStudies(navController, Destination.Onboarding)
        addLinkOnboarding(navController, Destination.Onboarding)
        addRecordingOnboarding(navController, Destination.Onboarding)
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
        addReportFormNested()
        addReportSubmittedNested()
    }
}
private fun NavGraphBuilder.addReportFormNested() {
    composable(
        route = NestedDestination.ReportFormNested.createRoute(Destination.ReportForm)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Report form")
        }

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
        addTermsAndConditions(navController, Destination.Settings)
        addStudies(navController, Destination.Settings)
        addEmail(navController, Destination.Onboarding)
        addLinkOnboarding(navController, Destination.Settings)
        addRecordingOnboarding(navController, Destination.Settings)
    }
}

private fun NavGraphBuilder.addSettingsNested(
    navController: NavController
) {
    composable(
        route = NestedDestination.SettingsNested.createRoute(Destination.Settings)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Settings home")
        }

    }
}


private fun NavGraphBuilder.addTermsAndConditions(
    navController: NavController,
    root: Destination
) {
    composable(
        route = NestedDestination.TermsAndConditions.createRoute(root)
    ) {
        when (root) {
            is Destination.Onboarding -> {
                // terms and conditions onboarding screen
                TermsAndConditionsScreen(
                    onNextScreen = {

                    }
                )
            }

            is Destination.Settings -> {
                // terms and conditions settings screen
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Settings terms and conditions")
                }
            }

            else -> Unit
        }
    }
}
private fun NavGraphBuilder.addStudies(
    navController: NavController,
    root: Destination
) {
    composable(
        route = NestedDestination.Studies.createRoute(root)
    ) {
        when (root) {
            is Destination.Onboarding -> {
                // studies onboarding screen
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Onboarding studies")
                }
            }

            is Destination.Settings -> {
                // studies settings screen
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Settings studies")
                }
            }

            else -> Unit
        }
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
private fun NavGraphBuilder.addLinkOnboarding(
    navController: NavController,
    root: Destination
) {
    composable(
        route = NestedDestination.Studies.createRoute(root)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Link onboarding")
        }

    }
}
private fun NavGraphBuilder.addRecordingOnboarding(
    navController: NavController,
    root: Destination
) {
    composable(
        route = NestedDestination.Studies.createRoute(root)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Recording onboarding")
        }

    }
}
