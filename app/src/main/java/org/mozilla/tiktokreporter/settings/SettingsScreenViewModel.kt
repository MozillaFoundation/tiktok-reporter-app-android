package org.mozilla.tiktokreporter.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.data.model.Policy
import org.mozilla.tiktokreporter.TikTokReporterRepository
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        viewModelScope.launch {
            val settingsEntries = mutableListOf<SettingsEntry>()

            settingsEntries.add(SettingsEntry.About)
            settingsEntries.add(SettingsEntry.Studies)

            val studyRes = tikTokReporterRepository.getSelectedStudy()
            if (studyRes.isSuccess) {
                val study = studyRes.getOrNull()!!

                if (study.onboarding?.form != null) {
                    settingsEntries.add(SettingsEntry.Email)
                }

                var hasTerms = false
                var hasPrivacyPolicy = false
                for (policy in study.policies) {
                    if (!hasTerms) {
                        hasTerms = policy.type == Policy.Type.TermsAndConditions
                    }
                    if (!hasPrivacyPolicy) {
                        hasPrivacyPolicy = policy.type == Policy.Type.Privacy
                    }

                    if (hasTerms && hasPrivacyPolicy) {
                        break
                    }
                }

                if (hasTerms) {
                    settingsEntries.add(SettingsEntry.Terms)
                }
                if (hasPrivacyPolicy) {
                    settingsEntries.add(SettingsEntry.Privacy)
                }
            }

            settingsEntries.add(SettingsEntry.DataHandling)

            _state.update { state ->
                state.copy(
                    entries = settingsEntries
                )
            }
        }
    }

    data class State(
        val entries: List<SettingsEntry> = emptyList(),
    )

    enum class SettingsEntry {
        About,
        Studies,
        Email,
        Terms,
        Privacy,
        DataHandling
    }
}