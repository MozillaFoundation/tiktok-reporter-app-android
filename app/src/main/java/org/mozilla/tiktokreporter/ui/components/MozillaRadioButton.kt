package org.mozilla.tiktokreporter.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

private val radioButtonSize = 20.dp
private val selectedStrokeWidth = 6.dp
private val unselectedStrokeWidth = 2.dp
private val animationDuration = 100

@Composable
fun MozillaRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selectedColor: Color = MozillaColor.Blue,
    unselectedColor: Color = MozillaColor.Inactive,
    disabledSelectedColor: Color = MozillaColor.BlueDisabled,
    disabledUnselectedColor: Color = MozillaColor.Disabled
) {
    val strokeWidth = animateDpAsState(
        targetValue = if (selected) selectedStrokeWidth else unselectedStrokeWidth,
        animationSpec = tween(durationMillis = animationDuration),
        label = "strokeWidthAnimation"
    )
    val targetColor = when {
        enabled && selected -> selectedColor
        enabled && !selected -> unselectedColor
        !enabled && selected -> disabledSelectedColor
        else -> disabledUnselectedColor
    }
    val color = if (enabled) {
        animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = animationDuration),
            label = "colorAnimation"
        )
    } else {
        rememberUpdatedState(targetColor)
    }

    val selectableModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = false,
                radius = radioButtonSize
            )
        )
    } else {
        Modifier
    }

    Canvas(
        modifier = modifier
            .then(selectableModifier)
            .wrapContentSize(Alignment.Center)
            .requiredSize(radioButtonSize),
        onDraw = {
            drawCircle(
                color = color.value,
                radius = radioButtonSize.div(2).toPx() - strokeWidth.value.div(2).toPx(),
                style = Stroke(
                    width = strokeWidth.value.toPx(),
                )
            )
        }
    )
}

@Preview(
    showBackground = true,
)
@Composable
private fun MozillaRadioButtonPreview1() {
    TikTokReporterTheme {
        Column {
            MozillaRadioButton(
                selected = false,
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
private fun MozillaRadioButtonPreview2() {
    TikTokReporterTheme {
        Column {
            MozillaRadioButton(
                selected = true,
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
private fun MozillaRadioButtonPreview3() {
    TikTokReporterTheme {
        Column {
            MozillaRadioButton(
                selected = false,
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
private fun MozillaRadioButtonPreview4() {
    TikTokReporterTheme {
        Column {
            MozillaRadioButton(
                selected = true,
                onClick = {},
                enabled = false
            )
        }
    }
}