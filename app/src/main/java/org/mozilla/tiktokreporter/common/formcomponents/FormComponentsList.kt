package org.mozilla.tiktokreporter.common.formcomponents

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.common.FormFieldUiComponent

@Composable
fun formComponentsItems(
    formFields: List<FormFieldUiComponent<*>>,
    onFormFieldValueChanged: (formFieldId: String, value: Any) -> Unit,
    coroutineScope: CoroutineScope? = null,
    scrollState: ScrollState? = null
) {
    formFields.filter { it.isVisible }.forEach { field ->
        when (field) {
            is FormFieldUiComponent.TextField -> {
                FormTextField(modifier = Modifier.fillMaxWidth(), field = field, onTextChanged = {
                    onFormFieldValueChanged(field.id, it)
                })
            }

            is FormFieldUiComponent.DropDown -> {
                FormDropDown(modifier = Modifier.fillMaxWidth(), field = field, onOptionChanged = {
                    onFormFieldValueChanged(field.id, it)
                })
            }

            is FormFieldUiComponent.Slider -> {
                FormSlider(modifier = Modifier.fillMaxWidth(), field = field, onValueChanged = {
                    onFormFieldValueChanged(field.id, it)
                })
            }
        }
    }

    if (formFields.indexOfFirst { it.error != null } >= 0 && coroutineScope != null && scrollState != null) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                scrollState.animateScrollTo(formFields.indexOfFirst { it.error != null })
            }
        }
    }
}