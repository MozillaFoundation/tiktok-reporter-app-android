package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MozillaDropdown(
    options: List<String>,
    text: String,
    onOptionSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
) {
    val focusManager = LocalFocusManager.current
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded,
        onExpandedChange = {
            isExpanded = !isExpanded
        },
    ) {
        MozillaTextField(
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(),
            text = text,
            onTextChanged = { },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
            },
            label = label,
            placeholder = placeholder
        )

        ExposedDropdownMenu(
            modifier = Modifier.background(Color.White),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEachIndexed { index, elem ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = elem,
                            style = MozillaTypography.Body1,
                        )
                    },
                    onClick = {
                        onOptionSelected(index, elem)
                        isExpanded = false
                        focusManager.clearFocus()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    colors = MenuDefaults.itemColors(
                        textColor = MozillaColor.TextColor,
                        leadingIconColor = MozillaColor.TextColor,
                        trailingIconColor = MozillaColor.TextColor,
                        disabledTextColor = MozillaColor.Disabled,
                        disabledLeadingIconColor = MozillaColor.Disabled,
                        disabledTrailingIconColor = MozillaColor.Disabled
                    )
                )
                if (index < options.lastIndex) {
                    Divider(
                        modifier = Modifier.fillMaxWidth().padding(ExposedDropdownMenuDefaults.ItemContentPadding),
                        thickness = 2.dp,
                        color = MozillaColor.Divider
                    )
                }
            }
        }
    }
}
