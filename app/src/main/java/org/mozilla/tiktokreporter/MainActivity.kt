package org.mozilla.tiktokreporter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.mozilla.tiktokreporter.navigation.NavContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TikTokReporterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MozillaColor.Background
                ) {
                    NavContainer()
                }
            }
        }
    }
}