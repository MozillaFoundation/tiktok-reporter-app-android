package org.mozilla.tiktokreporter.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

object Dimensions {
    object Spacing {
        val XS = 4.dp
        val S = 8.dp
        val M = 16.dp
        val L = 24.dp
        val XL = 32.dp
        val XXL = 48.dp
    }

    object Layout {
        val XXS = 16.dp
        val XS = 24.dp
        val S = 32.dp
        val M = 48.dp
        val L = 64.dp
        val XL = 96.dp
        val XXL = 192.dp
    }
}