package org.mozilla.tiktokreporter.datahandling

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mozilla.tiktokreporter.util.OneTimeEvent
import javax.inject.Inject

@HiltViewModel
class DataHandlingScreenViewModel @Inject constructor(
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun downloadData() {

    }

    fun deleteData() {

    }

    data class State(
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object NavigateBack: UiAction()
    }
}