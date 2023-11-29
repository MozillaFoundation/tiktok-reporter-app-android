package org.mozilla.tiktokreporter.studieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.TikTokReporterError
import org.mozilla.tiktokreporter.data.model.StudyOverview
import org.mozilla.tiktokreporter.TikTokReporterRepository
import org.mozilla.tiktokreporter.toTikTokReporterError
import javax.inject.Inject

@HiltViewModel
class StudiesListScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiAction = Channel<UiAction>()
    val uiAction = _uiAction.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.Unconfined) {
            _isLoading.update { true }

            val studiesResult = tikTokReporterRepository.fetchStudies()
            if (studiesResult.isFailure)  {
                _isLoading.update { false }
                _uiAction.send(
                    UiAction.ShowError(studiesResult.exceptionOrNull()!!.toTikTokReporterError())
                )
                return@launch
            }

            val studies = studiesResult.getOrNull().orEmpty()

            _isLoading.update { false }
            _state.update {
                it.copy(
                    studies = studies
                )
            }
        }
    }

    fun selectStudyAtIndex(selectedIndex: Int) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val newStudies = state.value.studies.mapIndexed { index, study ->
                study.copy(
                    isSelected = index == selectedIndex
                )
            }

            _state.update {
                it.copy(
                    studies = newStudies
                )
            }
        }
    }

    fun onSave(
        isForOnboarding: Boolean,
        shouldForceChange: Boolean
    ) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val selectedStudy = state.value.studies.first { it.isSelected }

            // settings screen - initial study change (before dialog confirmation)
            if (!isForOnboarding && !shouldForceChange) {
                if (selectedStudy.id != tikTokReporterRepository.selectedStudyId) {
                    _uiAction.send(UiAction.ShowChangeStudyWarning)
                }

                return@launch
            }

            tikTokReporterRepository.selectStudy(selectedStudy.id)
            _uiAction.send(
                if (selectedStudy.hasTerms) UiAction.OnGoToStudyTerms
                else if (selectedStudy.hasOnboarding) UiAction.OnGoToStudyOnboarding
                else if (selectedStudy.hasEmailForm) UiAction.OnGoToEmail
                else UiAction.OnGoToReportForm
            )
        }
    }

    data class State(
        val studies: List<StudyOverview> = emptyList()
    )

    sealed class UiAction {
        data class ShowError(
            val error: TikTokReporterError
        ): UiAction()
        data object ShowChangeStudyWarning : UiAction()
        data object OnGoToStudyOnboarding : UiAction()
        data object OnGoToStudyTerms : UiAction()
        data object OnGoToEmail : UiAction()
        data object OnGoToReportForm : UiAction()
    }
}