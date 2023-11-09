package org.mozilla.tiktokreporter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.mozilla.tiktokreporter.navigation.NavContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.sharedPreferences

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val onboardingCompleted by sharedPreferences(Common.PREFERENCES_ONBOARDING_COMPLETED_KEY, false)
    private val termsAccepted by sharedPreferences(Common.PREFERENCES_TERMS_ACCEPTED_KEY, false)
    private var firstAccess by sharedPreferences(Common.PREFERENCES_FIRST_ACCESS_KEY, true)

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TikTokReporterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MozillaColor.Background
                ) {
                    NavContainer(
                        onboardingCompleted = onboardingCompleted,
                        termsAccepted = termsAccepted,
                        firstAccess = firstAccess
                    )
                }
            }
        }

        firstAccess = false

        handleNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNewIntent(intent)
    }

    private fun handleNewIntent(intent: Intent?) {
        val data = intent?.extras?.getString(Intent.EXTRA_TEXT)
        println("@@@@@@ $this - data: $data")
        mainViewModel.onTikTokLinkShared(data)
    }
}