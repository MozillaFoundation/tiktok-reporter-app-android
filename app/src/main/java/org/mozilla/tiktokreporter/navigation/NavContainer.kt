package org.mozilla.tiktokreporter.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import kotlinx.coroutines.delay
import org.mozilla.tiktokreporter.onboarding.termsconditions.TermsAndConditionsScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavContainer() {

    val navController = rememberNavController()
    navController.addOnDestinationChangedListener { navController, destination, bundle ->
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
    val startDestination = NestedDestination.SplashScreenNested.createRoute(Destination.SplashScreen)
    navigation(
        route = Destination.SplashScreen.route,
        startDestination = startDestination
    ) {
        composable(
            route = startDestination
        ) {
            LaunchedEffect(
                key1 = Unit,
                block = {
                    delay(2000L)
                    val destination = NestedDestination.TermsAndConditions.createRoute(Destination.Onboarding)
                    navController.navigate(destination) {
                        launchSingleTop = true
                        popUpTo(startDestination) {
                            inclusive = true
                        }
                    }
                }
            )
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFD7279),
                                    Color(0xFFEC101A)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(maxWidth.value, 0f),
                            )
                        )
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(124.dp)
                        .align(Alignment.Center),
                )
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
