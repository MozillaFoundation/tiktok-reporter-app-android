package org.mozilla.tiktokreporter.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.size(124.dp)
                .align(Alignment.Center),
        )
    }
}