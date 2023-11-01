package org.mozilla.tiktokreporter.reportform

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mozilla.tiktokreporter.util.OneTimeEvent
import javax.inject.Inject

@HiltViewModel
class ReportFormScreenViewModel @Inject constructor(
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {

    }

    data class State(
        val a: String = "",
        val action: OneTimeEvent<UiAction>? = null
    )

    sealed class UiAction {
        data object ShowLoading: UiAction()
    }
}