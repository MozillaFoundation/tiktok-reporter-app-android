package org.mozilla.tiktokreporter.onboarding.termsconditions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.remote.TIkTokReporterService
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val tikTokReporterService: TIkTokReporterService
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val policy = tikTokReporterService.getAppTermsAndConditions().getOrNull(0)

            policy?.let { p ->
                _state.update {
                    it.copy(
                        title = p.title,
                        subtitle = p.subtitle,
                        content = p.text,
                    )
                }
            }
        }
    }

    data class State(
        val title: String = "",
        val subtitle: String = "",
        val content: String = ""
    )
}