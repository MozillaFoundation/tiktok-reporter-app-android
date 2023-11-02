package org.mozilla.tiktokreporter.reportform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.model.Form
import org.mozilla.tiktokreporter.data.model.FormField
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

            _state.update {
                it.copy(
                    supportsRecording = study.supportsRecording,
                    formFields = studyForm.fields,
                    action = null
                )
            }
        }
    }

    data class State(
        val supportsRecording: Boolean = false,
        val formFields: List<FormField> = listOf(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading: UiAction()
        data object ShowNoFormFound: UiAction()
    }
}