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

            val study = tikTokReporterRepository.getSelectedStudy()
            val onboarding = study.onboarding ?: kotlin.run {
                _state.update {
                    it.copy(
                        action = UiAction.GoToReportForm.toOneTimeEvent()
                    )
                }

                return@launch
            }

            _state.update {
                it.copy(
                    action = null,
//                    steps = onboarding.steps,
//                    form = onboarding.form,
                )
            }
        }
    }

    data class State(
        val steps: List<OnboardingStep> = listOf(
            OnboardingStep(
                id = "1",
                title = "Title 1",
                subtitle = "Subtitle 1",
                description = "Description 1",
                imageUrl = "https://storage.cloud.google.com/regrets_reporter_onboarding_docs/Onboarding%20image%20-%20recording%20-%20step%201.png",
                details = "Details 1",
                order = 1
            ),
            OnboardingStep(
                id = "2",
                title = "Title 2",
                subtitle = "Subtitle 2",
                description = "Description 2",
                imageUrl = "https://storage.cloud.google.com/regrets_reporter_onboarding_docs/Onboarding%20image%20-%20recording%20-%20step%202.png",
                details = "Details 2",
                order = 2
            ),
            OnboardingStep(
                id = "3",
                title = "Title 3",
                subtitle = "Subtitle 3",
                description = "Description 3",
                imageUrl = "https://storage.cloud.google.com/regrets_reporter_onboarding_docs/Onboarding%20image%20-%20recording%20-%20step%203.png",
                details = "Details 3",
                order = 3
            ),
            OnboardingStep(
                id = "4",
                title = "Title 4",
                subtitle = "Subtitle 4",
                description = "Description 4",
                imageUrl = "https://storage.cloud.google.com/regrets_reporter_onboarding_docs/Onboarding%20image%20-%20recording%20-%20step%204.png",
                details = "Details 4",
                order = 4
            ),
            OnboardingStep(
                id = "5",
                title = "Title 5",
                subtitle = "Subtitle 5",
                description = "Description 5",
                imageUrl = "https://storage.cloud.google.com/regrets_reporter_onboarding_docs/Onboarding%20image%20-%20recording%20-%20step%205.png",
                details = "Details 5",
                order = 5
            ),
            OnboardingStep(
                id = "6",
                title = "Title 6",
                subtitle = "Subtitle 6",
                description = "Description 6",
                imageUrl = "https://storage.cloud.google.com/regrets_reporter_onboarding_docs/Onboarding%20image%20-%20recording%20-%20step%206.png",
                details = "Details 6",
                order = 6
            ),
        ),
        val form: Form? = null,
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading: UiAction()
        data object GoToReportForm: UiAction()
    }
}