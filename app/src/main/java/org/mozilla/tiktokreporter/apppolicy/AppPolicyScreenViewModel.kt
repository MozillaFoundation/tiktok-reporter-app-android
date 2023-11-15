package org.mozilla.tiktokreporter.apppolicy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.model.Policy
import org.mozilla.tiktokreporter.navigation.NestedDestination
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import org.mozilla.tiktokreporter.util.OneTimeEvent
import org.mozilla.tiktokreporter.util.toOneTimeEvent
import javax.inject.Inject

@HiltViewModel
class AppPolicyScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var policyType = NestedDestination.AppPolicy.Type.TermsAndConditions

    init {
        val isForOnboarding = savedStateHandle.get<Boolean>("isForOnboarding") ?: true
        val type = savedStateHandle.get<String>("type").orEmpty()
        policyType = NestedDestination.AppPolicy.Type.valueOf(type)

        viewModelScope.launch(Dispatchers.Unconfined) {
            _isLoading.update { true }

            val policies = if (policyType == NestedDestination.AppPolicy.Type.Study || !isForOnboarding) {
                val selectedStudy = tikTokReporterRepository.getSelectedStudy()

                if (selectedStudy.isFailure) {
                    // TODO: map error
                    val err = selectedStudy.exceptionOrNull()!!
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            action = UiAction.ShowMessage(err.message.orEmpty()).toOneTimeEvent()
                        )
                    }
                    return@launch
                }

                selectedStudy.getOrNull()!!.policies
            } else {

                val policyResult = tikTokReporterRepository.getAppPolicies()
                if (policyResult.isFailure) {
                    // TODO: map error
                    val err = policyResult.exceptionOrNull()!!
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            action = UiAction.ShowMessage(err.message.orEmpty()).toOneTimeEvent()
                        )
                    }
                    return@launch
                }

                policyResult.getOrNull().orEmpty()
            }

            val policy = policies.firstOrNull { policy ->
                when (policyType) {
                    NestedDestination.AppPolicy.Type.TermsAndConditions -> policy.type == Policy.Type.TermsAndConditions
                    NestedDestination.AppPolicy.Type.PrivacyPolicy -> policy.type == Policy.Type.Privacy
                    NestedDestination.AppPolicy.Type.Study -> policy.type == Policy.Type.TermsAndConditions
                }
            } ?: kotlin.run {
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        action = UiAction.ShowNoPolicyFound.toOneTimeEvent()
                    )
                }
                return@launch
            }

            _isLoading.update { false }
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

    fun acceptTerms() {
        viewModelScope.launch {
            tikTokReporterRepository.acceptTermsAndConditions()

            _state.update { state ->
                state.copy(
                    action = if (policyType == NestedDestination.AppPolicy.Type.Study) UiAction.OnGoToStudyOnboarding.toOneTimeEvent()
                    else UiAction.OnGoToStudies.toOneTimeEvent()
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
        data object ShowNoPolicyFound : UiAction()
        data object OnGoToStudyOnboarding : UiAction()
        data object OnGoToStudies : UiAction()
        data class ShowMessage(
            val message: String
        ): UiAction()
    }
}