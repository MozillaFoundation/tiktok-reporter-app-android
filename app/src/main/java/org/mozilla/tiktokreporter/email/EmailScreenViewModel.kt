package org.mozilla.tiktokreporter.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.toUiComponents
import org.mozilla.tiktokreporter.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class EmailScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.update { true }

            val studyResult = tikTokReporterRepository.getSelectedStudy()
            if (studyResult.isFailure) {
                // TODO: map error
                _isLoading.update { false }
                return@launch
            }

            val onboardingForm = studyResult.getOrNull()?.onboarding?.form ?: kotlin.run {
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        action = UiAction.GoToReportForm.toOneTimeEvent()
                    )
                }

                return@launch
            }

            val userEmail = tikTokReporterRepository.userEmail
            val fields = onboardingForm.fields.toUiComponents().toMutableList().apply {
                if (userEmail.isNotBlank()) {
                    val index = this.indexOfFirst { it is FormFieldUiComponent.TextField }
                    val field = this[index] as FormFieldUiComponent.TextField
                    this[index] = field.copy(
                        value = userEmail
                    )
                }
            }
            _isLoading.update { false }
            _state.update { state ->
                state.copy(
                    formFields = fields,
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
            val fieldIndex = state.value.formFields.indexOfFirst { it.id == formFieldId }
            val newField = when (val field = state.value.formFields[fieldIndex]) {
                is FormFieldUiComponent.TextField -> {
                    field.copy(
                        value = value as String
                    )
                }
                is FormFieldUiComponent.Slider -> {
                    field.copy(
                        value = value as Int
                    )
                }
                is FormFieldUiComponent.DropDown -> {
                    field.copy(
                        value = value as String
                    )
                }
            }

            val newFields = state.value.formFields.toMutableList()
            newFields[fieldIndex] = newField

            _state.update {
                it.copy(
                    formFields = newFields
                )
            }
        }
    }

    fun onSaveEmail() {
        viewModelScope.launch {
            val emailField = state.value.formFields.firstOrNull { it is FormFieldUiComponent.TextField }
            val email = emailField?.value as String

            tikTokReporterRepository.saveUserEmail(email)

            _state.update { state ->
                state.copy(
                    action = UiAction.EmailSaved.toOneTimeEvent()
                )
            }
        }
    }

    data class State(
        val formFields: List<FormFieldUiComponent<*>> = emptyList(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object GoToReportForm: UiAction()
        data object EmailSaved: UiAction()
    }
}