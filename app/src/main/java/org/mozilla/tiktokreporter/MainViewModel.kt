package org.mozilla.tiktokreporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.repository.TikTokReporterRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {

    fun onTikTokLinkShared(url: String?) {
        viewModelScope.launch {
            tikTokReporterRepository.tikTokUrlShared(url)
        }
    }
}