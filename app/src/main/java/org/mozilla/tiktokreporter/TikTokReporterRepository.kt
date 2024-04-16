package org.mozilla.tiktokreporter

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.mozilla.tiktokreporter.data.model.Policy
import org.mozilla.tiktokreporter.data.model.StudyDetails
import org.mozilla.tiktokreporter.data.model.StudyOverview
import org.mozilla.tiktokreporter.data.model.toPolicy
import org.mozilla.tiktokreporter.data.model.toStudyDetails
import org.mozilla.tiktokreporter.data.model.toStudyOverview
import org.mozilla.tiktokreporter.data.remote.GCSService
import org.mozilla.tiktokreporter.data.remote.TikTokReporterService
import org.mozilla.tiktokreporter.data.remote.response.SignedUrlDTO
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.dataStore
import org.mozilla.tiktokreporter.util.sharedPreferences
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TikTokReporterRepository @Inject constructor(
    private val tikTokReporterService: TikTokReporterService,
    private val gcsService: GCSService,
    @ApplicationContext private val context: Context
) {
    var userEmail by context.sharedPreferences(
        name = Common.PREFERENCES_USER_EMAIL_KEY, defaultValue = ""
    )
        private set
    var selectedStudyId by context.sharedPreferences(
        name = Common.PREFERENCES_SELECTED_STUDY_KEY, defaultValue = ""
    )
        private set

    private var onboardingCompleted by context.sharedPreferences(
        name = Common.PREFERENCES_ONBOARDING_COMPLETED_KEY, defaultValue = false
    )
    private var termsAccepted by context.sharedPreferences(
        name = Common.PREFERENCES_TERMS_ACCEPTED_KEY, defaultValue = false
    )

    private var selectedStudy: StudyDetails? = null
    var signedUrl: SignedUrlDTO? = null

    private val _tikTokUrl = MutableSharedFlow<String?>(1)
    val tikTokUrl = _tikTokUrl.asSharedFlow()

    suspend fun getAppPolicies(): Result<List<Policy>> {
        val policies = try {
            withContext(Dispatchers.IO) {
                return@withContext tikTokReporterService.getAppTermsAndConditions().map { it.toPolicy() }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(policies)
    }

    suspend fun fetchStudies(): Result<List<StudyOverview>> {
        val remoteStudies = try {
            tikTokReporterService.getStudies().mapIndexed { index, study ->
                val isSelected = if (selectedStudyId.isBlank()) index == 0 else study.id == selectedStudyId

                study.toStudyOverview(
                    isSelected = isSelected
                )
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(remoteStudies)
    }

    private suspend fun fetchStudyById(studyId: String): Result<StudyDetails> {
        val remoteStudy = try {
            tikTokReporterService.getStudyById(studyId)
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(remoteStudy.toStudyDetails())
    }

    suspend fun getSelectedStudy(): Result<StudyDetails> {
        if (selectedStudy == null) {

            val study = fetchStudyById(selectedStudyId)

            if (study.isSuccess) {
                selectedStudy = study.getOrNull()!!
            }

            return study
        }

        return Result.success(selectedStudy!!)
    }

    suspend fun selectStudy(studyId: String) {
        withContext(Dispatchers.IO) {
            selectedStudyId = studyId
            selectedStudy = null
            onboardingCompleted = false
        }
    }

    suspend fun acceptTermsAndConditions() {
        withContext(Dispatchers.IO) {
            termsAccepted = true
        }
    }

    suspend fun setOnboardingCompleted(isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            onboardingCompleted = isCompleted
        }
    }

    fun reopenApp() {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(context.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    suspend fun tikTokUrlShared(url: String?) {
        _tikTokUrl.emit(url)
    }

    suspend fun saveUserEmail(email: String) {
        withContext(Dispatchers.IO) {
            userEmail = email
        }
    }

    suspend fun cancelReport() {
        context.dataStore.edit {
            it.remove(Common.DATASTORE_KEY_VIDEO_URI)
        }
    }

    suspend fun clearData() {
        withContext(Dispatchers.IO) {
            context.deleteSharedPreferences(Common.PREFERENCES_USER_EMAIL_KEY)
        }
    }

    suspend fun uploadRecording(
        recordingUri: Uri
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "tiktok_recording.mp4")
            context.contentResolver.openInputStream(recordingUri)?.use {
                it.copyTo(file.outputStream())
            } ?: return@withContext Result.failure(Exception("Open input stream failure"))
            try {
                signedUrl = tikTokReporterService.getSignedUrl()
                val videoBody: RequestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
                gcsService.uploadRecording(
                    url = signedUrl!!.url, file = videoBody
                )

                file.delete()

                context.dataStore.edit {
                    it[Common.DATASTORE_KEY_RECORDING_UPLOADED] = 1
                }

                return@withContext Result.success(true)
            } catch (e: Exception) {
                file.delete()

                context.dataStore.edit {
                    it[Common.DATASTORE_KEY_RECORDING_UPLOADED] = -1
                }
                return@withContext Result.failure(e)
            }
        }
    }
}