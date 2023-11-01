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
        fontSize = 2.sp,
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
    val TextLink = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W700,
        fontSize = 17.sp,
        lineHeight = 17.sp
    )
    val Interface = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
    val Interface50 = TextStyle(
        fontFamily = NunitoSans,
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
}

// patch studies/{id}
/**
 *
 * {
 *   "name": "Mobile study",
 *   "description": "Description",
 *   "isActive": true,
 *   "countryCodeIds": [
 *     "20a5730b-a3e0-43eb-8f9c-7be2ef201817"
 *   ],
 *   "policyIds": [
 *     "2cd1f78b-5a5a-4827-b794-fd86384a908d"
 *   ],
 *   "onboardingId": "065d300e-ea2b-46bd-8e09-3a3cc4aaae80",
 *   "formId": "3b939cb0-1748-467b-a90b-a3402d3f035e"
 * }
 *
 **/