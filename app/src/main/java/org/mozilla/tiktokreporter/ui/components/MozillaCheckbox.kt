package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

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
private fun MozillaCheckboxPreview1() {
    TikTokReporterTheme {
        Column {
            MozillaCheckbox(
                checked = false,
                onClick = {},
                enabled = true
            )
        }
    }
}
@Preview(
    showBackground = true,
)
@Composable
private fun MozillaCheckboxPreview2() {
    TikTokReporterTheme {
        Column {
            MozillaCheckbox(
                checked = true,
                onClick = {},
                enabled = true
            )
        }
    }
}
@Preview(
    showBackground = true,
)
@Composable
private fun MozillaCheckboxPreview3() {
    TikTokReporterTheme {
        Column {
            MozillaCheckbox(
                checked = false,
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
private fun MozillaCheckboxPreview4() {
    TikTokReporterTheme {
        Column {
            MozillaCheckbox(
                checked = true,
                onClick = {},
                enabled = false
            )
        }
    }
}