package org.mozilla.tiktokreporter.datahandling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.components.LoadingScreen
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.components.dialog.DialogContainer
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension

@Composable
fun DataHandlingScreen(
    viewModel: DataHandlingScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    DialogContainer { _ ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        when (state.action?.get()) {
            is DataHandlingScreenViewModel.UiAction.NavigateBack -> onNavigateBack()
            else -> Unit
        }

        if (isLoading) {
            LoadingScreen()
        } else {
            MozillaScaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    MozillaTopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        navItem = {
                            IconButton(
                                onClick = onNavigateBack
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "",
                                    tint = MozillaColor.TextColor
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(
                            PaddingValues(
                                horizontal = MozillaDimension.M,
                                vertical = MozillaDimension.L
                            )
                        ),
                    verticalArrangement = Arrangement.spacedBy(MozillaDimension.S)
                ) {
                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.button_download_my_data),
                        onClick = viewModel::downloadData
                    )

                    SecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.button_delete_my_data),
                        onClick = viewModel::deleteData
                    )
                }
            }
        }
    }
}