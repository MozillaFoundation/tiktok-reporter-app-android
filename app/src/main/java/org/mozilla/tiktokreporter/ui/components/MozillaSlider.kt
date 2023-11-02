package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun MozillaSlider(
    sliderPosition: Int,
    max: Int,
    step: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    leftLabel: String = "",
    rightLabel: String = ""
) {
    val actualSteps = max.div(step) - 1
    val labelColor = MozillaColor.TextColor
    val sliderColors = SliderDefaults.colors(
        thumbColor = MozillaColor.Red,
        activeTickColor = MozillaColor.Red,
        activeTrackColor = MozillaColor.Red,
        inactiveTickColor = MozillaColor.Red,
        inactiveTrackColor = MozillaColor.Divider
    )

    Column(
        modifier = modifier,
    ) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            steps = actualSteps,
            value = sliderPosition.toFloat(),
            valueRange = 0f..max.toFloat(),
            onValueChange = {
                onValueChanged(it.toInt())
            },
            colors = sliderColors
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (leftLabel.isNotBlank()) {
                Text(
                    text = leftLabel,
                    style = MozillaTypography.Body2,
                    color = labelColor
                )
            }
            if (rightLabel.isNotBlank()) {
                Text(
                    text = rightLabel,
                    style = MozillaTypography.Body2,
                    color = labelColor
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun MozillaSliderPreview() {
    TikTokReporterTheme {
        MozillaSlider(
            sliderPosition = 2,
            max = 5,
            step = 1,
            onValueChanged = { },
            leftLabel = "low",
            rightLabel = "high",
        )
    }
}