package org.mozilla.tiktokreporter.reportform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.common.formcomponents.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.TabModelType
import org.mozilla.tiktokreporter.common.formcomponents.toUiComponents
import org.mozilla.tiktokreporter.data.model.FormFieldType
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class ReportFormScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        val tikTokUrl = savedStateHandle.get<String>("tikTokUrl")
        println("@@@@@@ $tikTokUrl")

        viewModelScope.launch {
            _isLoading.update { true }

            val studyResult = tikTokReporterRepository.getSelectedStudy()
            if (studyResult.isFailure) {
                // TODO: map error
                _isLoading.update { false }
                return@launch
            }

            val study = studyResult.getOrNull() ?: kotlin.run {
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        action = UiAction.ShowFetchStudyError.toOneTimeEvent()
                    )
                }
                return@launch
            }
            if (!study.isActive) {
                tikTokReporterRepository.setOnboardingCompleted(false)
                _state.update {
                    it.copy(
                        action = UiAction.ShowStudyNotActive.toOneTimeEvent()
                    )
                }
                return@launch
            }

            val studyForm = study.form ?: kotlin.run {
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        action = UiAction.ShowNoFormFound.toOneTimeEvent()
                    )
                }
                return@launch
            }


            _isLoading.update { false }
            _state.update { state ->
                state.copy(
                    tabs = buildList {
                        add(TabModelType.ReportLink)
                        if (study.supportsRecording) {
                            add(TabModelType.RecordSession)
                        }
                    },
                    formFields = studyForm.fields.toUiComponents(),
                    action = null
                )
            }
        }

        viewModelScope.launch {
            tikTokReporterRepository.setOnboardingCompleted(true)
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

    data class State(
        val tabs: List<TabModelType> = emptyList(),
        val selectedTabIndex: Int = 0,
        val formFields: List<FormFieldUiComponent> = listOf(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowNoFormFound: UiAction()
        data object GoToReportSubmittedScreen: UiAction()
        data object ShowFetchStudyError: UiAction()
        data object ShowStudyNotActive: UiAction()
    }
}
