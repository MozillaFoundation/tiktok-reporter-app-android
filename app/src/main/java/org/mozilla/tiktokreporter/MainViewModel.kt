package org.mozilla.tiktokreporter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {

    private val _tikTokLinkState = MutableStateFlow<String?>(null)
    val tikTokLinkState = _tikTokLinkState.asStateFlow()

    fun onTikTokLinkShared(url: String?) {
        viewModelScope.launch {
            _tikTokLinkState.update { url }
        }
    }
}