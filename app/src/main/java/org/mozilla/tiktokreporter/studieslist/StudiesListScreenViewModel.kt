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

    init {
        viewModelScope.launch(Dispatchers.Unconfined) {
            _state.update {
                it.copy(
                    action = UiAction.ShowLoading.toOneTimeEvent()
                )
            }

            val studiesResult = tikTokReporterRepository.fetchStudies()
            if (studiesResult.isFailure)  {
                // TODO: map error
                return@launch
            }

            val studies = studiesResult.getOrNull() ?: kotlin.run {
                _state.update {
                    it.copy(
                        action = UiAction.ShowNoStudiesFound.toOneTimeEvent()
                    )
                }
                return@launch
            }

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

    fun onSave() {
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

            tikTokReporterRepository.selectStudy(selectedStudy.id)

            _state.update {
                it.copy(
                    action = UiAction.OnNextScreen.toOneTimeEvent()
                )
            }
        }
    }

    data class State(
        val studies: List<StudyOverview> = emptyList(),
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading : UiAction()
        data object ShowNoStudiesFound : UiAction()
        data object OnNextScreen : UiAction()
        data object OnNoStudySelected : UiAction()
    }
}