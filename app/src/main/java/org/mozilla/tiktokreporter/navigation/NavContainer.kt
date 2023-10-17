package org.mozilla.tiktokreporter.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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

@Composable
fun NavContainer() {

    val navController = rememberNavController()
    var isFullScreen by remember {
        mutableStateOf(false)
    }
    navController.addOnDestinationChangedListener { navController, destination, bundle ->
        println("New Destination: $destination")
        isFullScreen = destination.route?.contains(Destination.FULL_SCREEN) ?: false
    }

    val startDestination by remember {
        mutableStateOf(Destination.Onboarding.route)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isFullScreen) {
                BottomAppBar {

                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = startDestination
        ) {
            addOnBoarding(navController)
            addReportLink(navController)
            addRecordSession(navController)
            addSettings(navController)
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
 * REPORT LINK
 */
private fun NavGraphBuilder.addReportLink(
    navController: NavController
) {
    val destination = NestedDestination.ReportLinkNested.createRoute(Destination.ReportLink)
    navigation(
        route = Destination.Onboarding.route,
        startDestination = destination
    ) {
        addReportLinkNested()
    }
}
private fun NavGraphBuilder.addReportLinkNested() {
    composable(
        route = NestedDestination.ReportLinkNested.createRoute(Destination.ReportLink)
    ) {

    }
}


/**
 * RECORD SESSION
 */
private fun NavGraphBuilder.addRecordSession(
    navController: NavController
) {
    val destination = NestedDestination.RecordSessionNested.createRoute(Destination.RecordSession)
    navigation(
        route = Destination.Onboarding.route,
        startDestination = destination
    ) {
        addRecordSessionNested()
    }
}
private fun NavGraphBuilder.addRecordSessionNested() {
    composable(
        route = NestedDestination.RecordSessionNested.createRoute(Destination.RecordSession)
    ) {

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
        route = Destination.Onboarding.route,
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
            }

            is Destination.Settings -> {
                // terms and conditions settings screen
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
            }

            is Destination.Settings -> {
                // studies settings screen
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
            }

            is Destination.Settings -> {
                // email settings screen
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

    }
}
private fun NavGraphBuilder.addRecordingOnboarding(
    navController: NavController,
    root: Destination
) {
    composable(
        route = NestedDestination.Studies.createRoute(root)
    ) {

    }
}
