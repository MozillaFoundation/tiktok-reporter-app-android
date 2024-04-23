package org.mozilla.tiktokreporter.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun SplashScreen() {
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
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MozillaDimension.XL.times(2))
                .align(Alignment.Center),
            painter = painterResource(
                id = R.drawable.tt_reporter_logo_02_white
            ),
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    TikTokReporterTheme {
        SplashScreen()
    }
}