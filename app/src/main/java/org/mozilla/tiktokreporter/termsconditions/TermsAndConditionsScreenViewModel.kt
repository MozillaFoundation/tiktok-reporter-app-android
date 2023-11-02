package org.mozilla.tiktokreporter.termsconditions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsScreenViewModel @Inject constructor(
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

            val policyResult = tikTokReporterRepository.getAppTermsAndConditions()
            if (policyResult.isFailure) {
                // TODO: map error
                val err = policyResult.exceptionOrNull()!!
                _state.update {
                    it.copy(
                        action = UiAction.ShowMessage(err.message.orEmpty()).toOneTimeEvent()
                    )
                }
                return@launch
            }

            val policy = policyResult.getOrNull() ?: kotlin.run {
                _state.update {
                    it.copy(
                        action = UiAction.ShowNoPolicyFound.toOneTimeEvent()
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    action = null,
                    title = policy.title,
                    subtitle = policy.subtitle,
                    content = policy.text,
                )
            }

        }
    }

    data class State(
        val title: String = "",
        val subtitle: String = "",
        val content: String = "",
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading : UiAction()
        data object ShowNoPolicyFound : UiAction()
        data class ShowMessage(
            val message: String
        ): UiAction()
    }
}