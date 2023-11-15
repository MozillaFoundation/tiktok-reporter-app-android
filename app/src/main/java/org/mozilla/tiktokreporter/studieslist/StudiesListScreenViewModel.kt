package org.mozilla.tiktokreporter.studieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.model.StudyOverview
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class StudiesListScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Unconfined) {
            _isLoading.update { true }

            val studiesResult = tikTokReporterRepository.fetchStudies()
            if (studiesResult.isFailure)  {
                // TODO: map error
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        action = UiAction.ShowMessage(
                            studiesResult.exceptionOrNull()!!.message.orEmpty()
                        ).toOneTimeEvent()
                    )
                }
                return@launch
            }

            val studies = studiesResult.getOrNull().orEmpty()

            _isLoading.update { false }
            _state.update {
                it.copy(
                    action = null,
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

            val selectedStudy = state.value.studies.firstOrNull { it.isSelected }
            if (selectedStudy == null) {
                _state.update {
                    it.copy(
                        action = UiAction.OnNoStudySelected.toOneTimeEvent()
                    )
                }

                return@launch
            }

            if (!isForOnboarding && !shouldForceChange) {
                val action = if (selectedStudy.id != tikTokReporterRepository.selectedStudyId) {
                    UiAction.ShowChangeStudyWarning.toOneTimeEvent<UiAction>()
                } else null

                _state.update {
                    it.copy(
                        action = action
                    )
                }
                return@launch
            }

            tikTokReporterRepository.selectStudy(selectedStudy.id)
            _state.update {
                it.copy(
                    action = if (selectedStudy.hasTerms) UiAction.OnGoToStudyTerms.toOneTimeEvent()
                        else if (selectedStudy.hasOnboarding) UiAction.OnGoToStudyOnboarding.toOneTimeEvent()
                        else if (selectedStudy.hasEmailForm) UiAction.OnGoToEmail.toOneTimeEvent()
                        else UiAction.OnGoToReportForm.toOneTimeEvent()
                )
            }
        }
    }

    data class State(
        val studies: List<StudyOverview> = emptyList(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data class ShowMessage(
            val message: String
        ): UiAction()
        data object ShowChangeStudyWarning : UiAction()
        data object OnGoToStudyOnboarding : UiAction()
        data object OnGoToStudyTerms : UiAction()
        data object OnGoToEmail : UiAction()
        data object OnGoToReportForm : UiAction()
        data object OnNoStudySelected : UiAction()
    }
}