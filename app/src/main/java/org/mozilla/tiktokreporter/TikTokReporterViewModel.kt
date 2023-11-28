package org.mozilla.tiktokreporter

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.util.dataStore
import javax.inject.Inject

@HiltViewModel
class TikTokReporterViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository,
    @ApplicationContext context: Context
): ViewModel() {

    init {
        viewModelScope.launch {
            context.dataStore.edit {
                it.clear()
            }
        }
    }

    fun onTikTokLinkShared(url: String?) {
        viewModelScope.launch {
            tikTokReporterRepository.tikTokUrlShared(url)
        }
    }
}