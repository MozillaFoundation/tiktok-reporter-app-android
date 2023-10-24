package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.Shapes
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimaryVariant: Boolean = false
) {
    val color = when {
        enabled && !isPrimaryVariant -> MozillaColor.Blue
        !enabled && !isPrimaryVariant -> MozillaColor.BlueDisabled
        enabled && isPrimaryVariant -> MozillaColor.WarmRed
        else -> MozillaColor.WarmRedDisabled
    }

    val colors = ButtonDefaults.buttonColors(
        containerColor = color,
        contentColor = Color.White,
        disabledContainerColor = color.copy(alpha = .3f),
        disabledContentColor = Color.White,
    )
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        enabled = enabled,
        shape = RectangleShape,
        colors = colors
    ) {
        Text(
            text = text,
            style = MozillaTypography.Interface
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        disabledContentColor = Color.Black.copy(alpha = .3f)
    )
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        enabled = enabled,
        shape = RectangleShape,
        colors = colors,
        border = BorderStroke(
            1.dp,
            if (enabled) Color.Black else Color.Black.copy(alpha = .3f)
        )
    ) {
        Text(
            text = text,
            style = MozillaTypography.Interface
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4
)
@Composable
private fun PrimaryButtonPreview() {
    TikTokReporterTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            PrimaryButton(
                text = "Primary Button",
                onClick = {},
                enabled = true
            )
            PrimaryButton(
                text = "Primary Button",
                onClick = {},
                enabled = false
            )
            PrimaryButton(
                text = "Primary Button",
                onClick = {},
                enabled = true,
                isPrimaryVariant = true
            )
            PrimaryButton(
                text = "Primary Button",
                onClick = {},
                enabled = false,
                isPrimaryVariant = true
            )
            SecondaryButton(
                text = "Secondary Button",
                onClick = {},
                enabled = true
            )

            SecondaryButton(
                text = "Secondary Button",
                onClick = {},
                enabled = false
            )
        }
    }
}