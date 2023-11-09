package org.mozilla.tiktokreporter.reportform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.common.TabModelType
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.OTHER_CATEGORY_TEXT_FIELD_ID
import org.mozilla.tiktokreporter.common.OTHER_DROP_DOWN_OPTION_ID
import org.mozilla.tiktokreporter.common.toUiComponents
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

            tikTokReporterRepository.tikTokUrl
                .onSubscription { emit(null) }
                .collect { tikTokUrl ->
                    _isLoading.update { false }

                    val fields = studyForm.fields.toUiComponents().toMutableList().apply {
                        if (tikTokUrl != null) {
                            val index = this.indexOfFirst { it is FormFieldUiComponent.TextField }
                            val field = this[index] as FormFieldUiComponent.TextField
                            this[index] = field.copy(
                                readOnly = true,
                                value = tikTokUrl
                            )
                        }
                    }

                    _state.update { state ->
                        state.copy(
                            tabs = buildList {
                                add(TabModelType.ReportLink)
                                if (study.supportsRecording) {
                                    add(TabModelType.RecordSession)
                                }
                            },
                            formFields = fields,
                            action = null
                        )
                    }
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
            val fieldIndex = state.value.formFields.indexOfFirst { it.id == formFieldId }

            val newFields = state.value.formFields.toMutableList()
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
                    val selectedOption = field.options.firstOrNull { it.title == value }

                    val otherTextFieldIndex = newFields.indexOfFirst { it.id == OTHER_CATEGORY_TEXT_FIELD_ID }
                    if (otherTextFieldIndex >= 0) {
                        val otherTextField = newFields[otherTextFieldIndex] as FormFieldUiComponent.TextField
                        newFields[otherTextFieldIndex] = otherTextField.copy(
                            isVisible = selectedOption?.id == OTHER_DROP_DOWN_OPTION_ID
                        )
                    }

                    field.copy(
                        value = value as String
                    )
                }
            }
            newFields[fieldIndex] = newField

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

    fun onRecordSessionCommentsChanged(text: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    recordSessionComments = text
                )
            }
        }
    }

    fun onSubmitReport() {
        _state.update {
            it.copy(
                action = UiAction.GoToReportSubmittedScreen.toOneTimeEvent()
            )
        }
    }

    data class State(
        val tabs: List<TabModelType> = emptyList(),
        val selectedTabIndex: Int = 0,
        val formFields: List<FormFieldUiComponent<*>> = listOf(),
        val isRecording: Boolean = false,
        val recordSessionComments: String = "",
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowNoFormFound: UiAction()
        data object GoToReportSubmittedScreen: UiAction()
        data object ShowFetchStudyError: UiAction()
        data object ShowStudyNotActive: UiAction()
    }
}
