package org.mozilla.tiktokreporter.reportform

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer

@Composable
fun ReportFormScreen(
    viewModel: ReportFormScreenViewModel = hiltViewModel()
) {
    DialogContainer { dialogState ->

        val state by viewModel.state.collectAsStateWithLifecycle()

        ReportFormScreenContent(
            state = state,
            modifier = Modifier.fillMaxSize()
        )

    }
}

@Composable
private fun ReportFormScreenContent(
    state: ReportFormScreenViewModel.State,
    modifier: Modifier = Modifier
) {

}