package org.mozilla.tiktokreporter.datahandling

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.GleanMetrics.Pings
import org.mozilla.tiktokreporter.TikTokReporterRepository
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DataHandlingScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiAction = Channel<UiAction>()
    val uiAction = _uiAction.receiveAsFlow()

    fun downloadData() {
        // TODO: check if email is set and ping GLEAN
        viewModelScope.launch {
            if (tikTokReporterRepository.userEmail.isNotBlank()) {
                Pings.downloadData.submit()
            } else {
                _uiAction.send(UiAction.ShowNoEmailProvidedWarning)
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            tikTokReporterRepository.clearData()

            // TODO: ping GLEAN
        }
    }

    sealed class UiAction {
        data object NavigateBack: UiAction()
        data object ShowNoEmailProvidedWarning: UiAction()
    }
}