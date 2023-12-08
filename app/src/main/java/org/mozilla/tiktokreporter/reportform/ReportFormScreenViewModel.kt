package org.mozilla.tiktokreporter.reportform

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mozilla.tiktokreporter.TikTokReporterError
import org.mozilla.tiktokreporter.common.FormFieldError
import org.mozilla.tiktokreporter.common.TabModelType
import org.mozilla.tiktokreporter.common.FormFieldUiComponent
import org.mozilla.tiktokreporter.common.OTHER_CATEGORY_TEXT_FIELD_ID
import org.mozilla.tiktokreporter.common.OTHER_DROP_DOWN_OPTION_ID
import org.mozilla.tiktokreporter.common.toUiComponents
import org.mozilla.tiktokreporter.TikTokReporterRepository
import org.mozilla.tiktokreporter.data.model.GleanFormItem
import org.mozilla.tiktokreporter.data.model.GleanFormRequest
import org.mozilla.tiktokreporter.data.remote.response.toFormFieldDTO
import org.mozilla.tiktokreporter.toTikTokReporterError
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.dataStore
import org.mozilla.tiktokreporter.util.millisToMinSecString
import org.mozilla.tiktokreporter.util.toDateString
import org.mozilla.tiktokreporter.util.toTimeString
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ReportFormScreenViewModel @Inject constructor(
    private val tikTokReporterRepository: TikTokReporterRepository,
    @ApplicationContext context: Context,
    private val moshi: Moshi
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiAction = Channel<UiAction>()
    val uiAction = _uiAction.receiveAsFlow()

    private var initialFormFields = listOf<FormFieldUiComponent<*>>()

    private val mediaMetadataRetriever = MediaMetadataRetriever()

    init {
        viewModelScope.launch {
            tikTokReporterRepository.setOnboardingCompleted(true)
        }

        viewModelScope.launch {
            _isLoading.update { true }

            val studyResult = tikTokReporterRepository.getSelectedStudy()
            if (studyResult.isFailure) {
                _isLoading.update { false }
                val error = studyResult.exceptionOrNull()!!.toTikTokReporterError()
                _uiAction.send(UiAction.ShowError(error))
                return@launch
            }

            val study = studyResult.getOrNull() ?: kotlin.run {
                _isLoading.update { false }
                _uiAction.send(UiAction.ShowFetchStudyError)
                return@launch
            }
            if (!study.isActive) {
                tikTokReporterRepository.setOnboardingCompleted(false)
                _uiAction.send(UiAction.ShowStudyNotActive)
                return@launch
            }


            tikTokReporterRepository.tikTokUrl
                .onSubscription { emit(null) }
                .collect { tikTokUrl ->
                    _isLoading.update { false }

                    val fields = study.form?.fields.orEmpty().toUiComponents(tikTokUrl)
                    initialFormFields = fields

                    val tabs = buildList {
                        if (fields.isNotEmpty()) {
                            add(TabModelType.ReportLink)
                        }
                        if (study.supportsRecording) {
                            add(TabModelType.RecordSession)
                        }
                    }

                    _state.update { state ->
                        state.copy(
                            tabs = tabs,
                            selectedTab = tabs.firstOrNull()?.to(0),
                            formFields = fields,
                        )
                    }
                }
        }

        viewModelScope.launch {
            context.dataStore.data.map {
                it[Common.DATASTORE_KEY_VIDEO_URI]
            }
                .filterNotNull()
                .collect { videoUriString ->
                    val videoUri = videoUriString.toUri()

                    withContext(Dispatchers.IO) {
                        context.contentResolver.query(
                            videoUri,
                            arrayOf(
                                MediaStore.Video.Media.TITLE,
                                MediaStore.Video.Media.DURATION,
                                MediaStore.Video.Media.DATE_ADDED
                            ),
                            null,
                            null
                        )?.use { cursor ->
                            val titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                            val dateColumn =
                                cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)

                            if (cursor.moveToFirst()) {

                                mediaMetadataRetriever.setDataSource(context, videoUri)
                                val duration =
                                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                        ?.toLong() ?: 0L // ms

                                val name = cursor.getString(titleColumn)

                                val date = cursor.getLong(dateColumn)
                                val instant = Instant.ofEpochSecond(date)
                                val localDateTime =
                                    instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

                                _state.update { state ->
                                    state.copy(
                                        video = VideoModel(
                                            name = name,
                                            duration = duration.millisToMinSecString(),
                                            date = localDateTime.toDateString(),
                                            time = localDateTime.toTimeString(),
                                            uri = videoUri
                                        )
                                    )
                                }

                            } else {
                                // ok uri - no entry found
                                _state.update { state ->
                                    state.copy(
                                        video = null
                                    )
                                }
                            }
                        } ?: _state.update { state ->
                            state.copy(
                                video = null
                            )
                        }
                    }
                }
        }

        viewModelScope.launch {
            context.dataStore.data.map {
                it[Common.DATASTORE_KEY_IS_RECORDING]
            }.collect { isRecording ->
                _state.update { state ->
                    state.copy(
                        isRecording = isRecording ?: false
                    )
                }
            }
        }

        viewModelScope.launch {
            context.dataStore.data.map {
                it[Common.DATASTORE_KEY_RECORDING_NAME]
            }
                .filterNotNull()
                .collect { recordingName ->
                    submitRecordedSessionForm(
                        recordingName = recordingName
                    )

                    context.dataStore.edit {
                        it.remove(Common.DATASTORE_KEY_RECORDING_NAME)
                    }
                }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            _state.update {
                it.copy(
                    selectedTab = it.tabs.getOrNull(tabIndex)?.to(tabIndex)
                )
            }
        }
    }

    fun onFormFieldValueChanged(
        formFieldId: String,
        value: Any
    ) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val fieldIndex = state.value.formFields.indexOfFirst { it.id == formFieldId }

            val newFields = state.value.formFields.toMutableList()
            val newField = when (val field = state.value.formFields[fieldIndex]) {
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
                    val selectedOption = field.options.firstOrNull { it.title == value }

                    val otherTextFieldIndex =
                        newFields.indexOfFirst { it.id == OTHER_CATEGORY_TEXT_FIELD_ID }
                    if (otherTextFieldIndex >= 0) {
                        val otherTextField =
                            newFields[otherTextFieldIndex] as FormFieldUiComponent.TextField
                        newFields[otherTextFieldIndex] = otherTextField.copy(
                            isVisible = selectedOption?.id == OTHER_DROP_DOWN_OPTION_ID
                        )
                    }

                    field.copy(
                        value = value as String
                    )
                }
            }
            newFields[fieldIndex] = newField

            _state.update {
                it.copy(
                    formFields = newFields
                )
            }
        }
    }

    fun onRecordSessionCommentsChanged(text: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    recordSessionComments = text
                )
            }
        }
    }

    fun onSubmitReport() {
        viewModelScope.launch(Dispatchers.Unconfined) {

            _isLoading.update { true }

            when (state.value.selectedTab?.first) {
                TabModelType.ReportLink -> {
                    _state.update {
                        it.copy(
                            formFields = state.value.formFields.map { field ->
                                when (field) {
                                    is FormFieldUiComponent.TextField -> field.copy(error = null)
                                    is FormFieldUiComponent.DropDown -> field.copy(error = null)
                                    is FormFieldUiComponent.Slider -> field.copy(error = null)
                                }
                            }
                        )
                    }

                    val errors: Map<Int, FormFieldError> = getFormErrors()
                    if (errors.isNotEmpty()) {
                        // invalid form, update state

                        val newFields = state.value.formFields.toMutableList()
                        newFields.apply {
                            errors.forEach { (fieldIndex, error) ->
                                val newField = when (val field = this[fieldIndex]) {
                                    is FormFieldUiComponent.TextField -> field.copy(error = error)
                                    is FormFieldUiComponent.DropDown -> field.copy(error = error)
                                    is FormFieldUiComponent.Slider -> field.copy(error = error)
                                }

                                this[fieldIndex] = newField
                            }
                        }

                        _state.update {
                            it.copy(
                                formFields = newFields
                            )
                        }

                        _isLoading.update { false }
                        return@launch
                    }

                    submitReportLinkForm()
                }

                TabModelType.RecordSession -> {
                    if (state.value.video == null) {
                        _state.update { state ->
                            state.copy(
                                showSubmitNoVideoError = true
                            )
                        }

                        _isLoading.update { false }
                        return@launch
                    }

                    _uiAction.send(UiAction.StartUploadRecordingService(state.value.video!!.uri))
                }

                else -> Unit
            }
        }
    }

    private suspend fun submitReportLinkForm() {
        // TODO: serialize form and submit to glean

        val serializedForm = serializeReportLinkForm()

//        TopLevel.identifier.set(UUID.randomUUID())
//        TopLevel.name.set("name")
//        TopLevel.fields.set("stringified json of the form")
//        Pings.tiktokReport.submit()

        onCancelReport()
        _uiAction.send(UiAction.GoToReportSubmittedScreen)
        _isLoading.update { false }
    }

    private suspend fun submitRecordedSessionForm(
        recordingName: String
    ) {
        // TODO: serialize form and submit to glean
        val serializedForm = serializeRecordSessionForm()

        onCancelReport()
        _uiAction.send(UiAction.StopUploadRecordingService)
        _isLoading.update { false }
    }

    /**
     * @return map containing the error (value) for each field located index (key)
     */
    private fun getFormErrors(): Map<Int, FormFieldError> {

        val formFields = _state.value.formFields
        val errors = formFields.mapIndexedNotNull { index, field ->
            when (field) {
                is FormFieldUiComponent.TextField -> {
                    if (field.id != OTHER_CATEGORY_TEXT_FIELD_ID && field.isRequired && field.value.isEmpty()) {
                        return@mapIndexedNotNull index to FormFieldError.Empty
                    }

                    null
                }

                is FormFieldUiComponent.DropDown -> {
                    val selectedOption = field.options.firstOrNull { it.title == field.value }

                    if (field.isRequired) {

                        if (selectedOption?.id == OTHER_DROP_DOWN_OPTION_ID) {
                            val otherTextFieldIndex =
                                formFields.indexOfFirst { it.id == OTHER_CATEGORY_TEXT_FIELD_ID }
                            if (otherTextFieldIndex >= 0) {
                                val otherTextField =
                                    formFields[otherTextFieldIndex] as FormFieldUiComponent.TextField

                                if (otherTextField.value.isEmpty()) {
                                    return@mapIndexedNotNull otherTextFieldIndex to FormFieldError.EmptyCategory
                                }
                            }
                        } else {
                            if (field.value.isEmpty()) {
                                return@mapIndexedNotNull index to FormFieldError.Empty
                            }
                        }
                    }

                    null
                }

                is FormFieldUiComponent.Slider -> {
                    null
                }
            }
        }.toMap()

        return errors
    }

    fun onCancelReport() {
        viewModelScope.launch {
            tikTokReporterRepository.cancelReport()

            _state.update {
                it.copy(
                    formFields = initialFormFields,
                    video = null,
                    showSubmitNoVideoError = false,
                    recordSessionComments = ""
                )
            }
        }
    }

    fun checkVideoExists(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = _state.value.video?.uri ?: return@launch

            val file = DocumentFile.fromSingleUri(context, uri)
            if (file == null || !file.exists()) {
                _state.update { state ->
                    state.copy(
                        video = null
                    )
                }

                context.dataStore.edit {
                    it.remove(Common.DATASTORE_KEY_VIDEO_URI)
                    it.remove(Common.DATASTORE_KEY_IS_RECORDING)
                }
            }
        }

    }

    private suspend fun serializeReportLinkForm(): String {

        val selectedStudy = tikTokReporterRepository.getSelectedStudy().getOrNull()!!
        val gleanFields = selectedStudy.form?.fields.orEmpty().mapNotNull { formField ->
            val uiFormFieldValue = state.value.formFields.firstOrNull { uiFormField -> uiFormField.id == formField.id }?.value

            uiFormFieldValue?.let {
                GleanFormItem(
                    inputValue = uiFormFieldValue,
                    formItem = formField.toFormFieldDTO()
                )
            }
        }


        val jsonAdapter = moshi.adapter(GleanFormRequest::class.java)

        return jsonAdapter.toJson(
            GleanFormRequest(
                id = selectedStudy.id,
                name = selectedStudy.name,
                items = gleanFields
            )
        )
    }

    private fun serializeRecordSessionForm(): String {

        return ""
    }

    data class State(
        val tabs: List<TabModelType> = emptyList(),
        val selectedTab: Pair<TabModelType, Int>? = null,
        val formFields: List<FormFieldUiComponent<*>> = listOf(),
        val isRecording: Boolean = false,
        val recordSessionComments: String = "",
        val video: VideoModel? = null,
        val showSubmitNoVideoError: Boolean = false
    )

    data class VideoModel(
        val name: String,
        val duration: String,
        val date: String,
        val time: String,
        val uri: Uri
    )

    sealed class UiAction {
        data object GoToReportSubmittedScreen : UiAction()
        data object ShowFetchStudyError : UiAction()
        data object ShowStudyNotActive : UiAction()
        data class StartUploadRecordingService(
            val recordingUri: Uri
        ) : UiAction()

        data object StopUploadRecordingService : UiAction()
        data class ShowError(
            val error: TikTokReporterError
        ) : UiAction()
    }
}
