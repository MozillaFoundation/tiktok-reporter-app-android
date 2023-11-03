package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.data.model.FormField
import org.mozilla.tiktokreporter.ui.components.MozillaSlider
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@Composable
fun FormSlider(
    field: FormField.Slider,
    sliderPosition: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (field.label.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.label,
                style = MozillaTypography.Body1
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }
        if (field.description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = field.description,
                style = MozillaTypography.Body2
            )
            Spacer(modifier = Modifier.height(MozillaDimension.S))
        }

        MozillaSlider(
            modifier = Modifier.fillMaxWidth(),
            sliderPosition = sliderPosition,
            max = field.max,
            step = field.step,
            onValueChanged = onValueChanged,
            leftLabel = field.leftLabel,
            rightLabel = field.rightLabel
        )
    }
}