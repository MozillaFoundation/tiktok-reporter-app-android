package org.mozilla.tiktokreporter.reportform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.model.FormField
import org.mozilla.tiktokreporter.data.model.FormFieldType
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class ReportFormScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
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

            val study = studyResult.getOrNull()
            val studyForm = study?.form ?: kotlin.run {
                _state.update {
                    it.copy(
                        action = UiAction.ShowNoFormFound.toOneTimeEvent()
                    )
                }
                return@launch
            }

            _state.update { state ->
                state.copy(
                    tabs = buildList {
                        add(TabModelType.ReportLink)
                        if (study.supportsRecording) {
                            add(TabModelType.RecordSession)
                        }
                    },
                    formFields = studyForm.fields.mapNotNull { field ->
                        when (field) {
                            is FormField.TextField -> field.toUiComponent("")
                            is FormField.DropDown -> {
                                val selectedOption = field.options.firstOrNull { option -> option.id == field.selectedOptionId }
                                field.toUiComponent(selectedOption?.title ?: "")
                            }
                            is FormField.Slider -> field.toUiComponent(0)
                            else -> null
                        }
                    },
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

    fun onTabSelected(tabIndex: Int) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            _state.update {
                it.copy(
                    selectedTabIndex = tabIndex
                )
            }
        }
    }

    fun onSubmitReport() {

    }

    enum class TabModelType {
        ReportLink,
        RecordSession
    }
    data class FormFieldUiComponent(
        val formField: FormField,
        val value: Any
    )

    data class State(
        val tabs: List<TabModelType> = emptyList(),
        val selectedTabIndex: Int = 0,
        val formFields: List<FormFieldUiComponent> = listOf(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading: UiAction()
        data object ShowNoFormFound: UiAction()
        data object GoToReportSubmittedScreen: UiAction()
    }

    private fun FormField.toUiComponent(
        value: Any
    ): FormFieldUiComponent {
        return FormFieldUiComponent(
            formField = this,
            value = value
        )
    }
}
