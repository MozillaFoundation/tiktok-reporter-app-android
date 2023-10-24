package org.mozilla.tiktokreporter.ui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun MozillaTabRow(
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = MozillaColor.TextColor,
        indicator = {
            if (selectedTabIndex < it.size) {
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(it[selectedTabIndex]),
                    height = 4.dp,
                    color = MozillaColor.WarmRed
                )
            }
        },
        divider = { }
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedTabIndex
            Tab(
                selected = isSelected,
                onClick = {
                    onTabSelected(index)
                },
                text = {
                    Text(
                        text = tab,
                        style = MozillaTypography.Interface,
                        color = if (isSelected) MozillaColor.TextColor else MozillaColor.TextColor50
                    )
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL,
    showSystemUi = true
)
@Composable
private fun MozillaTabRowPreview() {
    TikTokReporterTheme {
        MozillaTabRow(
            tabs = listOf("Option 1", "Option 2"),
            onTabSelected = { _ -> },
            selectedTabIndex = 0
        )
    }
}
@Preview(
    showBackground = true,
    device = Devices.PIXEL_4_XL,
    showSystemUi = true
)
@Composable
private fun MozillaTabRowSinglePreview() {
    TikTokReporterTheme {
        MozillaTabRow(
            tabs = listOf("Option 1"),
            onTabSelected = { _ -> },
            selectedTabIndex = 0
        )
    }
}