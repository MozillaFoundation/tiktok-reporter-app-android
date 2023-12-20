package org.mozilla.tiktokreporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TikTokReporterViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {
    private val _navAction = Channel<NavAction>()
    val navAction = _navAction.receiveAsFlow()

    fun onTikTokLinkShared(url: String?) {
        viewModelScope.launch {
            url?.let {
                tikTokReporterRepository.tikTokUrlShared(url)
                _navAction.send(NavAction.GoToReportLinkForm(url))
            }
        }
    }

    sealed class NavAction {
        data class GoToReportLinkForm(
            val tikTokUrl: String
        ): NavAction()
    }
}