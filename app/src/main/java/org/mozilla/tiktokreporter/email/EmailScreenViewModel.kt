package org.mozilla.tiktokreporter.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.common.formcomponents.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.formcomponents.toUiComponents
import org.mozilla.tiktokreporter.data.model.FormFieldType
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class EmailScreenViewModel @Inject constructor(
    tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    action = UiAction.ShowLoading.toOneTimeEvent()
                )
            }

            val studyResult = tikTokReporterRepository.getSelectedStudy()
            if (studyResult.isFailure) {
                // TODO: map error
                return@launch
            }

            val onboardingForm = studyResult.getOrNull()?.onboarding?.form ?: kotlin.run {
                _state.update {
                    it.copy(
                        action = UiAction.GoToReportForm.toOneTimeEvent()
                    )
                }

                return@launch
            }

            _state.update { state ->
                state.copy(
                    formFields = onboardingForm.fields.toUiComponents(),
                    action = null
                )
            }
        }
    }

    fun onFormFieldValueChanged(
        formFieldId: String,
        value: Any
    ) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val fieldIndex = state.value.formFields.indexOfFirst { it.formField.id == formFieldId }
            val field = state.value.formFields[fieldIndex]
            val newField = when (field.formField.type) {
                FormFieldType.TextField -> {
                    field.copy(
                        value = value as String
                    )
                }
                FormFieldType.Slider -> {
                    field.copy(
                        value = value as Int
                    )
                }
                FormFieldType.DropDown -> {
                    field.copy(
                        value = value as String
                    )
                }
                else -> null
            }

            val newFields = state.value.formFields.toMutableList()
            newFields[fieldIndex] = newField!!

            _state.update {
                it.copy(
                    formFields = newFields
                )
            }
        }
    }

    fun onSaveEmail() {
        viewModelScope.launch {
//            val emailField = state.value.formFields.firstOrNull { it.formField.type == FormFieldType.TextField }

            // save email
//            val email = emailField?.value as String

            _state.update { state ->
                state.copy(
                    action = UiAction.NavigateBack.toOneTimeEvent()
                )
            }
        }
    }

    data class State(
        val formFields: List<FormFieldUiComponent> = emptyList(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading: UiAction()
        data object GoToReportForm: UiAction()
        data object NavigateBack: UiAction()
    }
}