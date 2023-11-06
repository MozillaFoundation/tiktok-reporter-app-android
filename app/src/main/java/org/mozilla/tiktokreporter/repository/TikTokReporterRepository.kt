package org.mozilla.tiktokreporter.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.tiktokreporter.data.model.Policy
import org.mozilla.tiktokreporter.data.model.StudyDetails
import org.mozilla.tiktokreporter.data.model.StudyOverview
import org.mozilla.tiktokreporter.data.model.toPolicy
import org.mozilla.tiktokreporter.data.model.toStudyDetails
import org.mozilla.tiktokreporter.data.model.toStudyOverview
import org.mozilla.tiktokreporter.data.remote.TikTokReporterService
import org.mozilla.tiktokreporter.util.Common
import org.mozilla.tiktokreporter.util.sharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TikTokReporterRepository @Inject constructor(
    private val tikTokReporterService: TikTokReporterService,
    @ApplicationContext private val context: Context
) {
    private var selectedStudyId by context.sharedPreferences(name = Common.PREFERENCES_SELECTED_STUDY_KEY, defaultValue = "")
    private var onboardingCompleted by context.sharedPreferences(name = Common.PREFERENCES_ONBOARDING_COMPLETED_KEY, defaultValue = false)
    private var termsAccepted by context.sharedPreferences(name = Common.PREFERENCES_TERMS_ACCEPTED_KEY, defaultValue = false)

    private var selectedStudy: StudyDetails? = null

    suspend fun getAppPolicies(): Result<List<Policy>> {
        val policies = try {
            withContext(Dispatchers.IO) {
                return@withContext tikTokReporterService.getAppTermsAndConditions()
                    .map { it.toPolicy() }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(policies)
    }

    suspend fun fetchStudies(): Result<List<StudyOverview>> {
        val remoteStudies = try {
            tikTokReporterService.getStudies()
                .map {
                    it.toStudyOverview(
                        isSelected = it.id == selectedStudyId
                    )
                }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(remoteStudies)
    }

    suspend fun fetchStudyById(studyId: String): Result<StudyDetails> {
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
}