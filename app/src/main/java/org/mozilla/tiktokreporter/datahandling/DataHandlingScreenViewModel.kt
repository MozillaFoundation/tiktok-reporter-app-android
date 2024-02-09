package org.mozilla.tiktokreporter.datahandling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mozilla.telemetry.glean.Glean
import org.mozilla.tiktokreporter.GleanMetrics.DownloadData
import org.mozilla.tiktokreporter.GleanMetrics.Pings
import org.mozilla.tiktokreporter.TikTokReporterRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DataHandlingScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiAction = Channel<UiAction>()
    val uiAction = _uiAction.receiveAsFlow()

    fun downloadData() {
        // TODO: check if email is set and ping GLEAN
        viewModelScope.launch {
            val email = tikTokReporterRepository.userEmail

            if (email.isNotBlank()) {
                DownloadData.identifier.set(UUID.fromString(tikTokReporterRepository.selectedStudyId))
                DownloadData.email.set(email)
                Pings.downloadData.submit()
            } else {
                _uiAction.send(UiAction.ShowNoEmailProvidedWarning)
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            tikTokReporterRepository.clearData()

            // deletion-request sent automatically
            Glean.setUploadEnabled(false)

            // allow user to report forms
            Glean.setUploadEnabled(true)
            _uiAction.send(UiAction.ShowDataDeleted)
        }
    }

    sealed class UiAction {
        data object NavigateBack : UiAction()
        data object ShowNoEmailProvidedWarning : UiAction()
        data object ShowDataDeleted : UiAction()
    }
}