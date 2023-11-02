package org.mozilla.tiktokreporter.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object MozillaColor {
    val Background = Color.White

    val Blue = Color(0xFF595CF3)
    val BlueDisabled = Color(0x4D595CF3)
    val BlueLink = Color(0xFF0D10BF)

    val Red = Color(0xFFFF4F5E)
    val RedDisabled = Color(0x4DFF4F5E)

    val Success = Color(0xFF005E5E)

    val Outline = Color(0xFF666666)
    val OutlineDisabled = Color(0x4D666666)

    val TextColor = Color.Black
    val Inactive = Color(0xFF999999)
    val Divider = Color(0xFFF0F0F0)

    val Disabled = Color(0x4D000000)

    val Error = Color(0xFFCC0011)
}

val MozillaColorScheme = lightColorScheme(
    primary = MozillaColor.Blue,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    outline = MozillaColor.Outline
)