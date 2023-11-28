package org.mozilla.tiktokreporter

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
class TikTokReporterActivity : ComponentActivity() {

    private val onboardingCompleted by sharedPreferences(Common.PREFERENCES_ONBOARDING_COMPLETED_KEY, false)
    private val termsAccepted by sharedPreferences(Common.PREFERENCES_TERMS_ACCEPTED_KEY, false)

    private val tikTokReporterViewModel: TikTokReporterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            TikTokReporterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MozillaColor.Background
                ) {
                    NavContainer(
                        onboardingCompleted = onboardingCompleted,
                        termsAccepted = termsAccepted
                    )
                }
            }
        }

        handleNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNewIntent(intent)
    }

    private fun handleNewIntent(intent: Intent?) {
        val data = intent?.extras?.getString(Intent.EXTRA_TEXT)
        tikTokReporterViewModel.onTikTokLinkShared(data)
    }
}