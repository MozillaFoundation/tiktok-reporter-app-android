package org.mozilla.tiktokreporter.studyonboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.model.Form
import org.mozilla.tiktokreporter.data.model.OnboardingStep
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class StudyOnboardingScreenViewModel @Inject constructor(
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

            val onboarding = studyResult.getOrNull()?.onboarding ?: kotlin.run {
                _state.update {
                    it.copy(
                        action = UiAction.GoToReportForm.toOneTimeEvent()
                    )
                }

                return@launch
            }

            _state.update { state ->
                state.copy(
                    action = null,
                    steps = onboarding.steps.sortedBy { it.order },
                    form = onboarding.form,
                )
            }
        }
    }

    data class State(
        val steps: List<OnboardingStep> = listOf(),
        val form: Form? = null,
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading: UiAction()
        data object GoToReportForm: UiAction()
    }
}