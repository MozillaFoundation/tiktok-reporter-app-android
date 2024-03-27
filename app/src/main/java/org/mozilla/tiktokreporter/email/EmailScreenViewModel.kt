package org.mozilla.tiktokreporter.email

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mozilla.tiktokreporter.GleanMetrics.DownloadData
import org.mozilla.tiktokreporter.GleanMetrics.Email
import org.mozilla.tiktokreporter.GleanMetrics.Pings
import org.mozilla.tiktokreporter.TikTokReporterError
import org.mozilla.tiktokreporter.TikTokReporterRepository
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.toUiComponents
import org.mozilla.tiktokreporter.data.model.Form
import org.mozilla.tiktokreporter.data.model.StudyDetails
import org.mozilla.tiktokreporter.datahandling.DataHandlingScreenViewModel
import org.mozilla.tiktokreporter.toTikTokReporterError
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmailScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiAction = Channel<UiAction>()
    val uiAction = _uiAction.receiveAsFlow()

    private val _refreshAction = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            _refreshAction.collect {

                _isLoading.update { true }

                val studyResult = tikTokReporterRepository.getSelectedStudy()
                if (studyResult.isFailure) {
                    _isLoading.update { false }
                    val error = studyResult.exceptionOrNull()!!.toTikTokReporterError()
                    _uiAction.send(UiAction.ShowError(error))
                    return@collect
                }

                val study = studyResult.getOrNull()
                val onboardingForm = study?.onboarding?.form ?: kotlin.run {
                    _isLoading.update { false }
                    _uiAction.send(UiAction.GoToReportForm)
                    return@collect
                }

                val userEmail = tikTokReporterRepository.userEmail
                val fields = onboardingForm.fields.toUiComponents().toMutableList().apply {
                    if (userEmail.isNotBlank()) {
                        val index = this.indexOfFirst { it is FormFieldUiComponent.TextField }
                        val field = this[index] as FormFieldUiComponent.TextField
                        this[index] = field.copy(
                            value = userEmail
                        )
                    }
                }

                val dataDownloadForm = study?.dataDownloadForm
                val dataDownloadFields = if (dataDownloadForm is Form) {
                        dataDownloadForm.fields.toUiComponents().toMutableList().apply {
                            if (userEmail.isNotBlank()) {
                                val index = this.indexOfFirst { it is FormFieldUiComponent.TextField }
                                val field = this[index] as FormFieldUiComponent.TextField
                                this[index] = field.copy(
                                    value = userEmail
                                )
                            }
                        }
                    }
                    else fields

                _isLoading.update { false }
                _state.update { state ->
                    state.copy(
                        studyDetails = study,
                        formFields = fields,
                        dataFormFields = dataDownloadFields
                    )
                }
            }
        }
    }

    fun onFormFieldValueChanged(
        formFieldId: String,
        value: Any,
        mode: EmailScreenMode
    ) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val formFields = if (mode == EmailScreenMode.SETTINGS_DATA_HANDLING) state.value.dataFormFields else state.value.formFields
            val fieldIndex = formFields.indexOfFirst { it.id == formFieldId }
            val newField = when (val field = formFields[fieldIndex]) {
                is FormFieldUiComponent.TextField -> {
                    field.copy(
                        value = value as String
                    )
                }
                is FormFieldUiComponent.Slider -> {
                    field.copy(
                        value = value as Int
                    )
                }
                is FormFieldUiComponent.DropDown -> {
                    field.copy(
                        value = value as String
                    )
                }
            }

            val newFields = formFields.toMutableList()
            newFields[fieldIndex] = newField

            if (mode == EmailScreenMode.SETTINGS_DATA_HANDLING) {
                _state.update {
                    it.copy(
                        dataFormFields = newFields
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        formFields = newFields
                    )
                }
            }
        }
    }

    fun onSaveEmail() {
        viewModelScope.launch {
            val emailField = state.value.formFields.firstOrNull { it is FormFieldUiComponent.TextField }
            val email = emailField?.value as String

            tikTokReporterRepository.saveUserEmail(email)

            Email.identifier.set(UUID.fromString(state.value.studyDetails?.id))
            Email.email.set(email)
            Pings.email.submit()

            _uiAction.send(UiAction.EmailSaved)
        }
    }

    fun onSaveDataHandlingEmail() {
        viewModelScope.launch {
            val emailField = state.value.dataFormFields.firstOrNull { it is FormFieldUiComponent.TextField }
            val email = emailField?.value.toString()
            if (email.isNotBlank()) {
                tikTokReporterRepository.saveUserEmail(email)

                DownloadData.identifier.set(UUID.fromString(tikTokReporterRepository.selectedStudyId))
                DownloadData.email.set(email)
                Pings.downloadData.submit()
                _uiAction.send(UiAction.ShowDataDownloaded)
                _uiAction.send(UiAction.EmailSaved)
            } else {
                // TODO: update state to show error on form field,
                // for now the user just can't submit
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshAction.update { !_refreshAction.value }
        }
    }

    data class State(
        val studyDetails: StudyDetails? = null,
        val formFields: List<FormFieldUiComponent<*>> = emptyList(),
        val dataFormFields: List<FormFieldUiComponent<*>> = emptyList()
    )

    sealed class UiAction {
        data object GoToReportForm: UiAction()
        data object EmailSaved: UiAction()
        data object ShowDataDownloaded: UiAction()
        data class ShowError(
            val error: TikTokReporterError
        ): UiAction()
    }
}