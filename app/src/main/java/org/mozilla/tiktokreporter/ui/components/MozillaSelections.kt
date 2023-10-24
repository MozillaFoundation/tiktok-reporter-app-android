package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun MozillaRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = RadioButtonDefaults.colors(
        selectedColor = MozillaColor.Blue,
        unselectedColor = MozillaColor.Outline,
        disabledSelectedColor = MozillaColor.BlueDisabled,
        disabledUnselectedColor = MozillaColor.OutlineDisabled
    )
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors
    )
}

@Composable
fun MozillaCheckbox(
    checked: Boolean,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = CheckboxDefaults.colors(
        checkedColor = MozillaColor.Blue,
        uncheckedColor = MozillaColor.Outline,
        checkmarkColor = Color.White,
        disabledCheckedColor = MozillaColor.BlueDisabled,
        disabledUncheckedColor = MozillaColor.OutlineDisabled,
    )
    Checkbox(
        checked = checked,
        onCheckedChange = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors
    )
}

@Preview(
    showBackground = true,
)
@Composable
private fun MozillaRadioButtonPreview() {
    TikTokReporterTheme {
        Column {
            MozillaRadioButton(
                selected = false,
                onClick = {},
                enabled = true
            )
            MozillaRadioButton(
                selected = true,
                onClick = {},
                enabled = true
            )
            MozillaRadioButton(
                selected = false,
                onClick = {},
                enabled = false
            )
            MozillaRadioButton(
                selected = true,
                onClick = {},
                enabled = false
            )
        }
    }
}
@Preview(
    showBackground = true,
)
@Composable
private fun MozillaCheckboxPreview() {
    TikTokReporterTheme {
        Column {
            MozillaCheckbox(
                checked = false,
                onClick = {},
                enabled = true
            )
            MozillaCheckbox(
                checked = true,
                onClick = {},
                enabled = true
            )
            MozillaCheckbox(
                checked = false,
                onClick = {},
                enabled = false
            )
            MozillaCheckbox(
                checked = true,
                onClick = {},
                enabled = false
            )
        }
    }
}