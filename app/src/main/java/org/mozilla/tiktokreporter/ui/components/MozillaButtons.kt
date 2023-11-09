package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimaryVariant: Boolean = false
) {
    val containerColor = when {
        !isPrimaryVariant -> MozillaColor.Blue
        else -> MozillaColor.Red
    }
    val disabledContainerColor = when {
        !isPrimaryVariant -> MozillaColor.BlueDisabled
        else -> MozillaColor.RedDisabled
    }

    val colors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = Color.White,
        disabledContainerColor = disabledContainerColor,
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
        contentColor = MozillaColor.TextColor,
        disabledContentColor = MozillaColor.Disabled
    )
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        enabled = enabled,
        shape = RectangleShape,
        colors = colors,
        border = BorderStroke(
            2.dp,
            if (enabled) MozillaColor.TextColor else MozillaColor.Disabled
        )
    ) {
        Text(
            text = text,
            style = MozillaTypography.Interface
        )
    }
}

@Preview(
    showBackground = true
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