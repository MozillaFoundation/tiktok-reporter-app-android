package org.mozilla.tiktokreporter.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.mozilla.tiktokreporter.R

private val ZillaSlab = FontFamily(
    Font(R.font.zillaslab_light, weight = FontWeight.W300),
    Font(R.font.zillaslab_regular, weight = FontWeight.W400),
    Font(R.font.zillaslab_semibold, weight = FontWeight.W600),
)
private val NunitoSans = FontFamily(
    Font(R.font.nunitosans_regular, weight = FontWeight.W400),
    Font(R.font.nunitosans_semibold, weight = FontWeight.W600),
    Font(R.font.nunitosans_bold, weight = FontWeight.W700),
)

object MozillaTypography {
    val H1 = TextStyle(
        fontFamily = ZillaSlab,
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 42.2.sp
    )
    val H2 = TextStyle(
        fontFamily = ZillaSlab,
        fontWeight = FontWeight.W300,
        fontSize = 28.sp,
        lineHeight = 43.2.sp
    )
    val H3 = TextStyle(
        fontFamily = ZillaSlab,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        lineHeight = 28.8.sp
    )
    val H4 = TextStyle(
        fontFamily = ZillaSlab,
        fontWeight = FontWeight.W400,
        fontSize = 22.sp,
        lineHeight = 28.4.sp
    )
    val H5 = TextStyle(
        fontFamily = ZillaSlab,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp,
        lineHeight = 26.sp
    )
    val H6 = TextStyle(
        fontFamily = ZillaSlab,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 15.6.sp
    )
    val Body1 = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
        lineHeight = 27.sp
    )
    val Body2 = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 18.2.sp
    )
    val Interface = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
    val Success = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W700,
        fontSize = 22.sp,
        lineHeight = 28.6.sp
    )
}